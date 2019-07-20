package acr18as.sheffield.ac.uk.takemeback.viewmodel;

import android.app.Application;

import java.util.List;

import acr18as.sheffield.ac.uk.takemeback.repository.SavedLocationRepository;
import acr18as.sheffield.ac.uk.takemeback.repository.VisitedLocationRepository;
import acr18as.sheffield.ac.uk.takemeback.roomdb.SavedLocation;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SavedLocationViewModel extends AndroidViewModel {

    private SavedLocationRepository repository;
    private LiveData<SavedLocation> lastSavedLocation;
    private LiveData<List<SavedLocation>> allLocations;

    public SavedLocationViewModel(@NonNull Application application) {
        super(application);
        repository = new SavedLocationRepository(application);
        allLocations = repository.getAllLocations();
        lastSavedLocation = repository.getLastSavedLocation();
    }

    public void insert(SavedLocation location) {
        repository.insert(location);
    }

    public void update(SavedLocation location) {
        repository.update(location);
    }

    public void delete(SavedLocation location) {
        repository.delete(location);
    }

    public LiveData<List<SavedLocation>> getAllLocations() {
        return allLocations;
    }

    public LiveData<SavedLocation> getLastSavedLocation() { return lastSavedLocation;}

    public void deleteAllLocations() {repository.deleteAllLocations();}
}
