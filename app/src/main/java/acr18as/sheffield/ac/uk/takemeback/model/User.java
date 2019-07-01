package acr18as.sheffield.ac.uk.takemeback.model;

import android.location.Location;

public class User {

    private Location userLocation;
    private Destination destination;

    public User() {
        this.userLocation = null;
        this.destination = new Destination();
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }
}
