package acr18as.sheffield.ac.uk.takemeback.repository;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import acr18as.sheffield.ac.uk.takemeback.UserClient;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserRepository {

    private static String TAG = "UserRepository";

    private Application mApplication;
    private Context mContext;

    public UserRepository(Application application, Context context) {
        mApplication = application;
        mContext = context;
    }

    public LiveData<Location> getUserLocation() {
        final MutableLiveData<Location> userLocation = new MutableLiveData<>();
        Location location = ((UserClient)mContext.getApplicationContext()).getUser().getUserLocation();
        userLocation.postValue(location);
        return userLocation;
    }

    public LiveData<Location> getUserDestinationLocation() {
        final MutableLiveData<Location> destinationLocation = new MutableLiveData<>();
        Location location = ((UserClient)mContext.getApplicationContext()).getUser().getDestination().getDestinationPoint();
        destinationLocation.postValue(location);
        return destinationLocation;
    }

    public void setUserLocation(Location location) {
        //location = ((UserClient)mContext.getApplicationContext()).getUser().getUserLocation();
        ((UserClient)mContext.getApplicationContext()).getUser().setUserLocation(location);
    }

    public void setUserDestinationLocation(Location location) {
        //Location location = ((UserClient)mContext.getApplicationContext()).getUser().getDestination().getDestinationPoint();
        ((UserClient)mContext.getApplicationContext()).getUser().getDestination().setDestinationPoint(location);
    }
}
