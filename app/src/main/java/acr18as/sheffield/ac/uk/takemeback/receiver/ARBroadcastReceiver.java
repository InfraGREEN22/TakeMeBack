package acr18as.sheffield.ac.uk.takemeback.receiver;

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
    public static String STATE = "UNDETECTED";
    public static boolean isSaved = false;
    private Context ctx;
    private User user;
    private MainActivity mainActivity;
    private String label;
    private Vibrator v;

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
        v = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
        //STATE = "UNDETECTED";
        fragment = MapFragment.getFragment();
    }

    private void handleUserActivity(int type, int confidence) {
        label = null;

        if(confidence > Constants.CONFIDENCE) {
            switch (type) {
                case DetectedActivity.IN_VEHICLE: {
                    label = "IN VEHICLE";
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    //TODO: ПОМЕНЯЙ НА WALKING ПОТОМ!!!!!!!!!!!!!!!!!
                    label = "ON FOOT";
                    if (user.getUserLocation() != null) {

                        // можно тупо тут вызвать метод saveCurrentLocation из MapFragment вместо всего этого

                        if (STATE.equals("UNDETECTED")) {
                            //fragment.saveCurrentLocation();
                            STATE = "DETECTED";
                        } else
                            break;
                    }

                    break;
                }
                case DetectedActivity.STILL: {
                    if (STATE.equals("DETECTED")) {
                        if (isSaved)
                            break;
                        else {
                            fragment.saveCurrentLocation();
                            isSaved = true;
                        }
                        //STATE = "DETECTED";
                    }
                    label = "STILL";
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    label = "UNKNOWN";
                    break;
                }
            }
        }

        if(label != null) {
            Log.i(TAG, "User activity: " + label + ", Confidence: " + confidence);
        }

        if (confidence > Constants.CONFIDENCE && label != null) {
            Toast.makeText(mainActivity.getApplicationContext(), "Activity: " + label, Toast.LENGTH_SHORT).show();
            // Vibrate for 500 milliseconds
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }*/
        }
    }

}
