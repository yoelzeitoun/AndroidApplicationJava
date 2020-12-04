package il.co.expertize.androidapplicationjava.Data;

import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import il.co.expertize.androidapplicationjava.MainActivity;
import il.co.expertize.androidapplicationjava.Models.Travel;
import il.co.expertize.androidapplicationjava.UI.AddTravelActivity;

public class TravelFirebaseDataSource {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Travels");
    static List<Object> list = new ArrayList<>();
    public boolean insertTravel(Travel travel)
    {
        list.add(travel);
        myRef.setValue(list);
        return true;
    }
}
