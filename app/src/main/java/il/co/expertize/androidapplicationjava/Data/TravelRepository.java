package il.co.expertize.androidapplicationjava.Data;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import il.co.expertize.androidapplicationjava.Models.Travel;

public class TravelRepository extends Application {
    TravelFirebaseDataSource firebaseDataSource = null;
    public  TravelRepository(){}
    public  TravelRepository(Context app)
    {
        firebaseDataSource = new TravelFirebaseDataSource();
    }

    public boolean insertTravel(Travel travel)
    {
        return firebaseDataSource.insertTravel(travel);
    }

    public LiveData<Boolean> getTravels() {

        return firebaseDataSource.getTravels();
    }
}
