package acr18as.sheffield.ac.uk.takemeback.viewmodel;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import acr18as.sheffield.ac.uk.takemeback.repository.UserRepository;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UserViewModel extends AndroidViewModel {

    public UserRepository userRepository;

    public UserViewModel(@NonNull Application application, Context context) {
        super(application);
        userRepository = new UserRepository(application, context);
    }

    public LiveData<Location> getUserLocation() {
        return userRepository.getUserLocation();
    }

    public LiveData<Location> getUserDestinationLocation() {
        return userRepository.getUserDestinationLocation();
    }

    public void setUserLocation(Location location) {
        userRepository.setUserLocation(location);
    }

    public void setUserDestinationLocation(Location location) {
        userRepository.setUserDestinationLocation(location);
    }
}
