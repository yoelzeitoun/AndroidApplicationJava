package il.co.expertize.androidapplicationjava.Data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import il.co.expertize.androidapplicationjava.Models.Travel;

public class TravelFirebaseDataSource {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Travels");

    public void insertTravel(Travel travel)
    {
        List<Object> list = new ArrayList<>();
        list.add(travel.getClientName());
        list.add(travel.getClientEmail());
        myRef.setValue(list);
    }
}
