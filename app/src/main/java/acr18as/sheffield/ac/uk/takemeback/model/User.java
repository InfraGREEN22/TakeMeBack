package acr18as.sheffield.ac.uk.takemeback.model;

import android.location.Location;

public class User {

    private Location userLocation;

    public User() {
        this.userLocation = null;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }
}
