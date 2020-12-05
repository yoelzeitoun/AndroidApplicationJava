package il.co.expertize.androidapplicationjava.Data;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import il.co.expertize.androidapplicationjava.MainActivity;
import il.co.expertize.androidapplicationjava.Models.Travel;
import il.co.expertize.androidapplicationjava.UI.AddTravelActivity;

import static android.content.ContentValues.TAG;

public class TravelFirebaseDataSource {
    private final MutableLiveData<Boolean> isSuccess= new MutableLiveData<>();


    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference travels = firebaseDatabase.getReference("Travels");

    TravelFirebaseDataSource() {}

    private static TravelFirebaseDataSource instance;

    public static TravelFirebaseDataSource getInstance() {
        if (instance == null)
            instance = new TravelFirebaseDataSource();
        return instance;
    }

    public void addTravel(Travel travel) {
        String id = travels.push().getKey();
        travel.setTravelId(id);
        assert id != null;
        travels.child(id).setValue(travel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                isSuccess.setValue(true);
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isSuccess.setValue(false);
            }
        });
    }

    public MutableLiveData<Boolean> getIsSuccess() {return isSuccess;}
}

//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference myRef = database.getReference("Travels");
//    static List<Object> list = new ArrayList<>();
//    public boolean insertTravel(Travel travel)
//    {
//        list.add(travel);
//        myRef.setValue(list);
//        return true;
//
//    }
//
//    public MutableLiveData<Boolean> getTravels() {
//        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
//                return new LiveData<Boolean>();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//                return;
//            }
//        });
//    }
//}
