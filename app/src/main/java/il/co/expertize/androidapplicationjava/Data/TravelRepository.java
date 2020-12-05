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

    public void addTravel(Travel travel)
    {
        firebaseDataSource.addTravel(travel);
    }

    public LiveData<Boolean> getIsSuccess() {
        return firebaseDataSource.getIsSuccess();
    }
}
