package il.co.expertize.androidapplicationjava.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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

        button = (Button)findViewById(R.id.button);
        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        viewModel = new ViewModelProvider(this).get(TravelViewModel.class);
    }

    public void SendRequest(View view)
    {
        Travel travel = new Travel();
        travel.setClientName(name.getText().toString());
        travel.setClientEmail(email.getText().toString());
        viewModel.insertTravel(travel);
    }

}