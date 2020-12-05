package il.co.expertize.androidapplicationjava.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import il.co.expertize.androidapplicationjava.MainActivity;
import il.co.expertize.androidapplicationjava.Models.Travel;
import il.co.expertize.androidapplicationjava.R;
import il.co.expertize.androidapplicationjava.ViewModel.TravelViewModel;

public class AddTravelActivity extends AppCompatActivity {
    Button button;
    EditText name;
    EditText email;
    private TravelViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_travel_activity);
        button = (Button) findViewById(R.id.button);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        viewModel = new ViewModelProvider(this).get(TravelViewModel.class);

        final LiveData<Boolean> isSuccess = viewModel.getIsSuccess();
        isSuccess.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                Toast.makeText(AddTravelActivity.this, "Good Job!", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void SendRequest(View view) {
        if (name.getText().toString().isEmpty() || email.getText().toString().isEmpty())
            Toast.makeText(this, "Please fill all the informations", Toast.LENGTH_LONG).show();
        else {
            Travel travel = new Travel();
            travel.setClientName(name.getText().toString());
            name.setText("");
            travel.setClientEmail(email.getText().toString());
            email.setText("");
            viewModel.addTravel(travel);
        }
    }
}