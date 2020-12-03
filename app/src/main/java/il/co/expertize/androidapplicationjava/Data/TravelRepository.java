package il.co.expertize.androidapplicationjava.Data;

import android.app.Application;
import android.content.Context;

import il.co.expertize.androidapplicationjava.Models.Travel;

public class TravelRepository extends Application {
    TravelFirebaseDataSource firebaseDataSource = null;
    public  TravelRepository(){}
    public  TravelRepository(Context app)
    {
        firebaseDataSource = new TravelFirebaseDataSource();
    }

    public void insertTravel(Travel travel)
    {
        firebaseDataSource.insertTravel(travel);
    }
}
