package acr18as.sheffield.ac.uk.takemeback.repository;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import acr18as.sheffield.ac.uk.takemeback.roomdb.SavedLocation;
import acr18as.sheffield.ac.uk.takemeback.roomdb.SavedLocationDAO;
import acr18as.sheffield.ac.uk.takemeback.roomdb.SavedLocationRoomDatabase;
import androidx.lifecycle.LiveData;

public class SavedLocationRepository {

    private SavedLocationDAO mLocationDAO;
    private LiveData<List<SavedLocation>> mAllLocations;
    private LiveData<SavedLocation> mLastSavedLocation;

    public SavedLocationRepository(Application application) {
        SavedLocationRoomDatabase db = SavedLocationRoomDatabase.getInstance(application);
        mLocationDAO = db.savedLocationDAO();
        mAllLocations = mLocationDAO.getAllLocations();
        mLastSavedLocation = mLocationDAO.getLastSavedLocation();
    }


    public void insert(SavedLocation location) {
        new InsertLocationAsyncTask(mLocationDAO).execute(location);
    }


    public void update(SavedLocation location) {
        new UpdateLocationAsyncTask(mLocationDAO).execute(location);
    }


    public void delete(SavedLocation location) {
        new DeleteLocationAsyncTask(mLocationDAO).execute(location);
    }

    public void deleteAllLocations() {
        new DeleteAllLocationsAsyncTask(mLocationDAO).execute();
    }

    public LiveData<List<SavedLocation>> getAllLocations() {
        return mAllLocations;
    }

    public LiveData<SavedLocation> getLastSavedLocation() { return mLastSavedLocation; }


    //-------------- ASYNC TASKS ------------------------//


    private static class DeleteAllLocationsAsyncTask extends AsyncTask<Void, Void, Void> {
        private SavedLocationDAO mLocationDAO;

        DeleteAllLocationsAsyncTask(SavedLocationDAO dao) {
            this.mLocationDAO = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mLocationDAO.deleteAllLocations();
            return null;
        }
    }


    public static class InsertLocationAsyncTask extends AsyncTask<SavedLocation, Void, Void> {

        private SavedLocationDAO mLocationDAO;

        InsertLocationAsyncTask(SavedLocationDAO locationDAO) {
            this.mLocationDAO = locationDAO;
        }

        @Override
        protected Void doInBackground(SavedLocation... locations) {
            mLocationDAO.insert(locations[0]);
            return null;
        }
    }


    public static class UpdateLocationAsyncTask extends AsyncTask<SavedLocation, Void, Void> {

        private SavedLocationDAO mLocationDAO;

        UpdateLocationAsyncTask(SavedLocationDAO locationDAO) {
            this.mLocationDAO = locationDAO;
        }

        @Override
        protected Void doInBackground(SavedLocation... locations) {
            mLocationDAO.update(locations[0]);
            return null;
        }
    }


    public static class DeleteLocationAsyncTask extends AsyncTask<SavedLocation, Void, Void> {

        private SavedLocationDAO mLocationDAO;

        DeleteLocationAsyncTask(SavedLocationDAO locationDAO) {
            this.mLocationDAO = locationDAO;
        }

        @Override
        protected Void doInBackground(SavedLocation... locations) {
            mLocationDAO.delete(locations[0]);
            return null;
        }
    }
}
