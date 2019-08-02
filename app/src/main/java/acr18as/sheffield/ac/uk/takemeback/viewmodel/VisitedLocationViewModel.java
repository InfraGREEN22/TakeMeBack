package acr18as.sheffield.ac.uk.takemeback.viewmodel;

import android.app.Application;
import android.location.Location;

import java.util.List;

import acr18as.sheffield.ac.uk.takemeback.repository.VisitedLocationRepository;
import acr18as.sheffield.ac.uk.takemeback.roomdb.VisitedLocation;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class VisitedLocationViewModel extends AndroidViewModel {

    private VisitedLocationRepository repository;

    private LiveData<List<VisitedLocation>> allLocations;

    public VisitedLocationViewModel(@NonNull Application application) {
        super(application);
        repository = new VisitedLocationRepository(application);
        allLocations = repository.getAllLocations();
    }

    public void insert(VisitedLocation location) {
        repository.insert(location);
    }

    public void update(VisitedLocation location) {
        repository.update(location);
    }

    public void delete(VisitedLocation location) {
        repository.delete(location);
    }

    public LiveData<List<VisitedLocation>> getAllLocations() {
        return allLocations;
    }

    public void deleteAllLocations() {repository.deleteAllLocations();}

    public LiveData<List<VisitedLocation>> getLocationsByType(String type) { return repository.getLocationsByType(type); }

    public LiveData<Integer> getLocationsTypeCount(String type) { return repository.getLocationsTypeCount(type); }

    public LiveData<Integer> getLocationsCount() { return repository.getLocationsCount(); }
}
