package acr18as.sheffield.ac.uk.takemeback.model;

import android.location.Location;

public class Destination {

    private Location destinationPoint;
    private String type; // a car, a bus stop, a train stop

    public Destination() {
        this.destinationPoint = null;
    }

    public Location getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(Location destinationPoint) {
        this.destinationPoint = destinationPoint;
    }
}
