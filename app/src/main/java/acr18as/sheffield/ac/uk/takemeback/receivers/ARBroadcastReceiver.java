package acr18as.sheffield.ac.uk.takemeback.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import acr18as.sheffield.ac.uk.takemeback.Constants;
import acr18as.sheffield.ac.uk.takemeback.UserClient;
import acr18as.sheffield.ac.uk.takemeback.model.User;
import acr18as.sheffield.ac.uk.takemeback.view.MainActivity;
import acr18as.sheffield.ac.uk.takemeback.view.MapFragment;

public class ARBroadcastReceiver extends BroadcastReceiver {

    private static String TAG = "ARBroadcastReceiver";
    private Context ctx;
    private User user;
    private MainActivity mainActivity;

    private Context getCtx() {
        return ctx;
    }

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
    }

    private void handleUserActivity(int type, int confidence) {
        String label = null;

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = "IN VEHICLE";
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = "ON FOOT";
                if(user.getUserLocation() != null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(user.getUserLocation().getLatitude(), user.getUserLocation().getLongitude());
                    markerOptions.position(latLng);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    MapFragment.googleMap.addMarker(markerOptions);
                }

                Vibrator v = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
                break;
            }
            case DetectedActivity.STILL: {
                label = "STILL";
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = "UNKNOWN";
                break;
            }
        }

        Log.e(TAG, "User activity: " + label + ", Confidence: " + confidence);

        if (confidence > Constants.CONFIDENCE) {
            Toast.makeText(mainActivity.getApplicationContext(), "Activity: " + label, Toast.LENGTH_SHORT).show();
        }
    }
}
