package il.co.expertize.androidapplicationjava.UI;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;

import il.co.expertize.androidapplicationjava.Models.Travel;
import il.co.expertize.androidapplicationjava.R;
import il.co.expertize.androidapplicationjava.ViewModel.TravelViewModel;

import static il.co.expertize.androidapplicationjava.Data.Utils.Utils.PrintColorToast;
import static il.co.expertize.androidapplicationjava.Data.Utils.Utils.isNumeric;


public class AddTravelActivity extends AppCompatActivity {
    Button button;
    EditText name;
    EditText email;
    EditText phone_number;
    EditText departure_address;
    EditText destination_address;
    EditText number_of_passengers;
    DatePicker departure_date;
    DatePicker return_date;

    private TravelViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_travel_activity);
        button = (Button) findViewById(R.id.button);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        phone_number=(EditText) findViewById(R.id.phone) ;
        departure_address=(EditText) findViewById(R.id.DepartureAddress);
        destination_address=(EditText) findViewById(R.id.DestinationAddress);
        number_of_passengers=(EditText) findViewById(R.id.numOfPassengers);
        departure_date= (DatePicker) findViewById(R.id.DepartureDate);
        return_date= (DatePicker) findViewById(R.id.ReturnDate);

        viewModel = new ViewModelProvider(this).get(TravelViewModel.class);

        final LiveData<Boolean> isSuccess = viewModel.getIsSuccess();
        isSuccess.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                if (isSuccess.getValue())
                    PrintColorToast(getApplicationContext(),"Good Job My friend!!", Color.GREEN);
                else PrintColorToast(getApplicationContext(),"Data not entered properly", Color.RED);
            }
        });
    }

    public void SendRequest(View view) {
        Date departureDate = new Date(departure_date.getYear(),departure_date.getMonth(),departure_date.getDayOfMonth());
        Date returnDate = new Date(return_date.getYear(),return_date.getMonth(),return_date.getDayOfMonth());
        if (name.getText().toString().isEmpty() || email.getText().toString().isEmpty() ||
        phone_number.getText().toString().isEmpty() || number_of_passengers.getText().toString().isEmpty()||
        departure_address.getText().toString().isEmpty()|| destination_address.getText().toString().isEmpty())
        {
            PrintColorToast(getApplicationContext(),"Please fill all the information",Color.RED);
        }
        else if (!isNumeric(phone_number.getText().toString())||!isNumeric(number_of_passengers.getText().toString()))
            PrintColorToast(getApplicationContext(),"Please enter Numbers",Color.RED);

        else if(returnDate.before(departureDate)||returnDate.equals(departureDate))
            PrintColorToast(getApplicationContext(),"Please enter return date later than the departure date",Color.RED);
        else {
            Travel travel = new Travel();
            travel.setClientName(name.getText().toString());
            name.setText("");
            travel.setClientEmail(email.getText().toString());
            email.setText("");
            travel.setClientPhone(phone_number.getText().toString());
            phone_number.setText("");
            travel.setNumberOfPassenger(number_of_passengers.getText().toString());
            number_of_passengers.setText("");
            travel.setDeparture_address(departure_address.getText().toString());
            departure_address.setText("");
            travel.setDestination_address(destination_address.getText().toString());
            destination_address.setText("");
            travel.setDeparture_date(departureDate);
            travel.setReturn_date(returnDate);
            viewModel.addTravel(travel);
        }
    }
}