package acr18as.sheffield.ac.uk.takemeback.repository;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import acr18as.sheffield.ac.uk.takemeback.roomdb.VisitedLocation;
import acr18as.sheffield.ac.uk.takemeback.roomdb.VisitedLocationDAO;
import acr18as.sheffield.ac.uk.takemeback.roomdb.VisitedLocationRoomDatabase;
import androidx.lifecycle.LiveData;

public class VisitedLocationRepository {

    private VisitedLocationDAO mLocationsDAO;
    private LiveData<List<VisitedLocation>> mAllLocations;

    public VisitedLocationRepository(Application application) {
        VisitedLocationRoomDatabase db = VisitedLocationRoomDatabase.getInstance(application);
        mLocationsDAO = db.locationDAO();
        mAllLocations = mLocationsDAO.getAllLocations();
    }


    public void insert(VisitedLocation location) {
        new InsertLocationAsyncTask(mLocationsDAO).execute(location);
    }


    public void update(VisitedLocation location) {
        new UpdateLocationAsyncTask(mLocationsDAO).execute(location);
    }


    public void delete(VisitedLocation location) {
        new DeleteLocationAsyncTask(mLocationsDAO).execute(location);
    }


    public LiveData<List<VisitedLocation>> getAllLocations() {
        return mAllLocations;
    }


    /*public LiveData<List<VisitedLocation>> search(String key) {
        return mLocationsDAO.search(key);
    }*/


    public static class InsertLocationAsyncTask extends AsyncTask<VisitedLocation, Void, Void> {

        private VisitedLocationDAO locationDAO;

        InsertLocationAsyncTask(VisitedLocationDAO locationDAO) {
            this.locationDAO = locationDAO;
        }

        @Override
        protected Void doInBackground(VisitedLocation... locations) {
            locationDAO.insert(locations[0]);
            return null;
        }
    }


    public static class UpdateLocationAsyncTask extends AsyncTask<VisitedLocation, Void, Void> {

        private VisitedLocationDAO locationDAO;

        UpdateLocationAsyncTask(VisitedLocationDAO locationDAO) {
            this.locationDAO = locationDAO;
        }

        @Override
        protected Void doInBackground(VisitedLocation... locations) {
            locationDAO.update(locations[0]);
            return null;
        }
    }


    public static class DeleteLocationAsyncTask extends AsyncTask<VisitedLocation, Void, Void> {

        private VisitedLocationDAO locationDAO;

        DeleteLocationAsyncTask(VisitedLocationDAO locationDAO) {
            this.locationDAO = locationDAO;
        }

        @Override
        protected Void doInBackground(VisitedLocation... locations) {
            locationDAO.delete(locations[0]);
            return null;
        }
    }
}
