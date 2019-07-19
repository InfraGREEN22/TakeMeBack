package acr18as.sheffield.ac.uk.takemeback.model;

import android.location.Location;

public class User {

    private Location userLocation;
    private Location destinationLocation;

    public User() {
        this.userLocation = null;
        this.destinationLocation = null;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public Location getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(Location location) {
        this.destinationLocation = location;
    }
}
