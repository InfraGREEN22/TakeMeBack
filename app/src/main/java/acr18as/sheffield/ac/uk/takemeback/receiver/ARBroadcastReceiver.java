package acr18as.sheffield.ac.uk.takemeback.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;

import acr18as.sheffield.ac.uk.takemeback.Constants;
import acr18as.sheffield.ac.uk.takemeback.R;
import acr18as.sheffield.ac.uk.takemeback.UserClient;
import acr18as.sheffield.ac.uk.takemeback.model.User;
import acr18as.sheffield.ac.uk.takemeback.places.GetPlacesAsyncTask;
import acr18as.sheffield.ac.uk.takemeback.roomdb.SavedLocation;
import acr18as.sheffield.ac.uk.takemeback.view.MainActivity;
import acr18as.sheffield.ac.uk.takemeback.view.MapFragment;
import acr18as.sheffield.ac.uk.takemeback.viewmodel.SavedLocationViewModel;
import acr18as.sheffield.ac.uk.takemeback.viewmodel.UserViewModel;
import acr18as.sheffield.ac.uk.takemeback.viewmodelfactory.UserViewModelFactory;
import androidx.lifecycle.ViewModelProviders;

public class ARBroadcastReceiver extends BroadcastReceiver {

    private static String TAG = "ARBroadcastReceiver";
    public static String STATE = "UNDETECTED";
    public static boolean isSaved = false;
    private Context ctx;
    private User user;
    private MainActivity mainActivity;
    private String label;
    private Vibrator v;
    private SavedLocationViewModel savedLocationViewModel;
    private UserViewModel userViewModel;
    private String placeType = null;

    private Context getCtx() {
        return ctx;
    }
    private MapFragment fragment = null;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
            int type = intent.getIntExtra("type", -1);
            int confidence = intent.getIntExtra("confidence", 0);
            handleUserActivity(type, confidence);
        }
        //ctx = context;
        user = ((UserClient)mainActivity.getApplicationContext()).getUser();

        // providing necessary viewmodels
        UserViewModelFactory factory = new UserViewModelFactory(mainActivity.getApplication(), context);
        userViewModel = ViewModelProviders.of(mainActivity, factory).get(UserViewModel.class);
        savedLocationViewModel = ViewModelProviders.of((mainActivity)).get(SavedLocationViewModel.class);

        v = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
        fragment = MapFragment.getFragment();
    }

    private void handleUserActivity(int type, int confidence) {
        label = null;

        if(confidence > Constants.CONFIDENCE) {
            switch (type) {
                case DetectedActivity.IN_VEHICLE: {
                    label = "IN VEHICLE";
                    if (user.getUserLocation() != null) {
                        if (STATE.equals("UNDETECTED")) {
                            STATE = "DETECTED";
                        } else
                            break;
                    }

                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    label = "ON FOOT";
                    if (STATE.equals("DETECTED")) {
                        saveCurrentLocation();
                        STATE = "UNDETECTED";
                    }
                    break;
                }
            }
        }

        if(label != null) {
            Log.i(TAG, "User activity: " + label + ", Confidence: " + confidence);
        }

        if (confidence > Constants.CONFIDENCE && label != null) {
            Toast.makeText(mainActivity.getApplicationContext(), "Activity: " + label, Toast.LENGTH_SHORT).show();
        }
    }

    public void saveCurrentLocation() {
        try {
            userViewModel.getUserLocation().observe(mainActivity, location -> {

                userViewModel.setUserDestinationLocation(location);

                getNearbyPlaces(location);
                SavedLocation savedLocation = new SavedLocation(location.getLatitude(), location.getLongitude(), placeType);
                try {
                    savedLocationViewModel.insert(savedLocation);
                    Toast.makeText(mainActivity, "Your current location has been saved at Lat: " + savedLocation.getLat()
                            + " Long: " + savedLocation.getLon(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(mainActivity, "Sorry, but the location cannot be saved...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });

        }
        catch (Exception e) {
            Toast.makeText(mainActivity.getApplicationContext(), "Something has gone wrong with saving...", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getNearbyPlaces(Location location) {
        String[] types = {"bus_station", "train_station", "parking"};
        try {
            for (String type : types) {
                GetPlacesAsyncTask getPlacesTask = new GetPlacesAsyncTask();
                String url = mainActivity.getResources().getString(R.string.nearestplacessearch) +
                        "location=" + location.getLatitude() + "," + location.getLongitude() + "&radius=10&type=" + type +
                        "&key=" + mainActivity.getResources().getString(R.string.google_maps_key);
                String[] stringArray = new String[]{url};
                getPlacesTask.execute(stringArray);
                if (getPlacesTask.isFound()) {
                    placeType = getPlacesTask.getPlaceType();
                    return;
                }
            }
            placeType = "street";
        }
        catch (Exception e) {
            Log.e(TAG, "Error in getting nearby places: " + e.toString());
        }
    }
}
