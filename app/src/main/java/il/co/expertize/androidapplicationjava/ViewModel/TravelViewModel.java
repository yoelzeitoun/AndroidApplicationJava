package il.co.expertize.androidapplicationjava.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import il.co.expertize.androidapplicationjava.Data.TravelRepository;
import il.co.expertize.androidapplicationjava.Models.Travel;

public class TravelViewModel extends AndroidViewModel {

    TravelRepository travelRepository;

    public TravelViewModel(@NonNull Application application) {
        super(application);
        travelRepository = new TravelRepository(application);
    }
    public void insertTravel(Travel travel) {
        travelRepository.insertTravel(travel);
    }
}
