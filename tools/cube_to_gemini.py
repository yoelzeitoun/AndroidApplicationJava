#!/usr/bin/env python3
"""Cube ACR -> Gemini sans API, via evenements de fichier (inotify).

Termux: pkg install python ffmpeg termux-api inotify-tools
          termux-setup-storage

Le processus reste en attente BLOQUANTE (sans balayage periodique):
les traitements sont lances uniquement apres CLOSE_WRITE / MOVED_TO.
Android peut toutefois suspendre ou arreter Termux en arriere-plan.

Le partage Android est demande, mais le script ne selectionne pas Gemini
et ne valide pas le message automatiquement.
"""
import argparse
import hashlib
import json
import re
import shutil
import subprocess
import sys
from datetime import datetime
from pathlib import Path

DEFAULT_DIR = Path.home() / 'storage/shared/Documents/CubeCallRecorder/All'
WORK = Path.home() / '.cube_to_gemini'
OUT = WORK / 'prepared_audio'
STATE = WORK / 'share_launched.json'
EXTS = {'.amr', '.m4a', '.mp3', '.wav', '.ogg', '.aac'}
PATTERN = re.compile(r'^(?:phone|call)_(\d{8})-(\d{6})_(.+)$', re.I)


def run(command, **kwargs):
    return subprocess.run(command, check=True, **kwargs)


def normalize(phone):
    digits = re.sub(r'\D+', '', phone or '')
    if digits.startswith('00972'):
        digits = '0' + digits[5:]
    elif digits.startswith('972'):
        digits = '0' + digits[3:]
    return digits


def find_contact(number):
    if not number:
        return 'Inconnu'
    try:
        result = run(['termux-contact-list'], capture_output=True, text=True, timeout=15)
        contacts = json.loads(result.stdout)
    except (subprocess.CalledProcessError, subprocess.TimeoutExpired, FileNotFoundError, ValueError):
        return 'Inconnu'
    target = normalize(number)
    matches = []
    for contact in contacts:
        value = normalize(contact.get('number', ''))
        if value == target or (len(target) >= 9 and len(value) >= 9 and value[-9:] == target[-9:]):
            matches.append(contact.get('name') or 'Inconnu')
    return matches[0] if len(set(matches)) == 1 and matches else 'Inconnu'


def parse_info(source):
    match = PATTERN.match(source.stem)
    if match:
        date, clock, raw_phone = match.groups()
        dt = datetime.strptime(date + clock, '%Y%m%d%H%M%S')
        phone = raw_phone.replace('_', '+', 1) if raw_phone.startswith('_') else raw_phone
    else:
        dt = datetime.fromtimestamp(source.stat().st_mtime)
        phone = 'Non disponible'
    return dt, phone


def safe(text):
    return re.sub(r'[\\/:*?"<>|\r\n]+', '_', text).strip()[:60] or 'Inconnu'


def prepare(source):
    dt, phone = parse_info(source)
    name = find_contact(phone)
    OUT.mkdir(parents=True, exist_ok=True)
    token = hashlib.sha1(str(source).encode('utf-8')).hexdigest()[:8]
    title = f"Appel_{dt:%Y-%m-%d_%H-%M-%S}_{safe(name)}_{safe(phone)}_{token}.m4a"
    target = OUT / title
    if not target.exists():
        if source.suffix.lower() == '.m4a':
            shutil.copy2(source, target)
        else:
            run(['ffmpeg', '-nostdin', '-hide_banner', '-loglevel', 'error', '-y',
                 '-i', str(source), '-vn', '-c:a', 'aac', '-b:a', '64k', str(target)],
                timeout=180)
    prompt = (f"Analyse cet enregistrement d'appel.\n"
              f"Contact : {name}\nNuméro : {phone}\n"
              f"Date et heure de l'appel : {dt:%d/%m/%Y %H:%M:%S}\n\n"
              "1. Transcris fidèlement l'appel dans sa langue d'origine.\n"
              "2. Résume les points importants.\n"
              "3. Liste les décisions, engagements, tâches et échéances.\n"
              "4. Signale les éléments incertains sans les inventer.")
    return target, prompt


def share(source):
    target, prompt = prepare(source)
    run(['termux-clipboard-set'], input=prompt, text=True, timeout=15)
    print(f"\nAudio prêt : {target.name}")
    print('Prompt copié dans le presse-papiers. Sélectionne Gemini puis colle le prompt.')
    run(['termux-open', '--send', '--chooser', '--content-type', 'audio/mp4', str(target)], timeout=15)
    print('Feuille de partage demandée (pas de confirmation de dépôt dans Gemini).')


def key(source):
    info = source.stat()
    return f'{source.name}|{info.st_size}|{info.st_mtime_ns}'


def recordings(folder):
    return sorted((f for f in folder.iterdir() if f.is_file() and f.suffix.lower() in EXTS),
                  key=lambda f: f.stat().st_mtime)


def save_state(state):
    WORK.mkdir(parents=True, exist_ok=True)
    temp = STATE.with_suffix('.tmp')
    temp.write_text(json.dumps(sorted(state), ensure_ascii=False), encoding='utf-8')
    temp.replace(STATE)


def listen(folder, state, events_only=False):
    """Attend des changements du dossier via le noyau, sans polling."""
    exe = shutil.which('inotifywait')
    if not exe:
        sys.exit('Installe inotify : pkg install inotify-tools')

    command = [exe, '--monitor', '--quiet',
               '--event', 'close_write', '--event', 'moved_to',
               '--format', '%f', '--', str(folder)]
    print(f'En attente d un NOUVEL enregistrement dans : {folder}', flush=True)
    print('Aucun scan periodique. Ctrl+C pour arreter.', flush=True)
    if events_only:
        print('Mode diagnostic : seulement afficher les evenements.', flush=True)

    while True:
        # Pas de boucle de polling : readline() attend un evenement inotify.
        with subprocess.Popen(command, stdout=subprocess.PIPE,
                              stderr=subprocess.PIPE, text=True,
                              bufsize=1) as watcher:
            try:
                for line in watcher.stdout:
                    filename = line.rstrip('\n')
                    if not filename:
                        continue
                    source = folder / filename
                    if source.suffix.lower() not in EXTS or not source.is_file():
                        continue
                    if events_only:
                        print(f'Evenement : {source.name}', flush=True)
                        continue
                    try:
                        if source.stat().st_size == 0:
                            continue
                        fingerprint = key(source)
                        if fingerprint in state:
                            continue
                        share(source)
                        state.add(fingerprint)
                        save_state(state)
                    except Exception as exc:
                        print(f'Erreur pour {source.name}: {exc}', file=sys.stderr, flush=True)
                # Ne pas relancer silencieusement en boucle sur une erreur d acces.
                watcher.wait(timeout=2)
                details = watcher.stderr.read().strip()
                sys.exit(f'Surveillance terminee (code {watcher.returncode}): {details}')
            except KeyboardInterrupt:
                watcher.terminate()
                try:
                    watcher.wait(timeout=2)
                except subprocess.TimeoutExpired:
                    watcher.kill()
                print('Surveillance arretee.')
                return


def main():
    parser = argparse.ArgumentParser(
        description='Partage Cube ACR vers Gemini uniquement a la fin de nouveaux fichiers')
    parser.add_argument('--dir', type=Path, default=DEFAULT_DIR,
                        help='Dossier audio Cube ACR')
    parser.add_argument('--test', action='store_true',
                        help='Partager le dernier enregistrement, sans surveillance')
    parser.add_argument('--events-only', action='store_true',
                        help='Tester les evenements sans ouvrir de partage')
    args = parser.parse_args()
    folder = args.dir.expanduser()
    if not folder.is_dir():
        print(f'Dossier introuvable ou inaccessible : {folder}', file=sys.stderr)
        print('Verifie le dossier Cube ACR puis utilise --dir CHEMIN.', file=sys.stderr)
        sys.exit(1)

    files = recordings(folder)
    if args.test:
        if not files:
            sys.exit('Aucun enregistrement trouve.')
        share(files[-1])
        return

    WORK.mkdir(parents=True, exist_ok=True)
    try:
        state = set(json.loads(STATE.read_text(encoding='utf-8'))) if STATE.exists() else set()
    except (OSError, ValueError):
        state = set()
    # Ignorer les fichiers existants au demarrage; uniquement les nouveaux evenements.
    state.update(key(file) for file in files)
    if not args.events_only:
        save_state(state)
    listen(folder, state, events_only=args.events_only)


if __name__ == '__main__':
    main()
