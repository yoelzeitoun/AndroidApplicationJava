package il.co.expertize.androidapplicationjava.UI;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import il.co.expertize.androidapplicationjava.Models.Travel;
import il.co.expertize.androidapplicationjava.R;
import il.co.expertize.androidapplicationjava.ViewModel.TravelViewModel;

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
                    PrintColorToast("Good Job My friend!!", Color.GREEN);
                else PrintColorToast("Data not entered properly", Color.RED);
            }
        });
    }


    public void SendRequest(View view) {
        if (name.getText().toString().isEmpty() || email.getText().toString().isEmpty())
        {
            PrintColorToast("Please fill all the information",Color.RED);
        }
            //Toast.makeText(this, "", Toast.LENGTH_LONG).show();
        else {
            Travel travel = new Travel();
            travel.setClientName(name.getText().toString());
            name.setText("");
            travel.setClientEmail(email.getText().toString());
            email.setText("");
            travel.setClientPhone(phone_number.getText().toString());
            phone_number.setText("");
            travel.setReturn_date(departure_date);


            viewModel.addTravel(travel);
        }
    }

    /**
     * Function that prints a toast message with entered color
     * @param msg the message we want to print
     * @param color the color of the message
     */
    public void PrintColorToast(String msg, Integer color)
    {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
        toastMessage.setTextColor(color);
        toast.show();
    }
}