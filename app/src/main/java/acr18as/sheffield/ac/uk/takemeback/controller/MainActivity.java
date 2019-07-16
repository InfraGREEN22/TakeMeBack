package acr18as.sheffield.ac.uk.takemeback.controller;

import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import acr18as.sheffield.ac.uk.takemeback.Constants;
import acr18as.sheffield.ac.uk.takemeback.R;
import acr18as.sheffield.ac.uk.takemeback.UserClient;
import acr18as.sheffield.ac.uk.takemeback.adapters.PagerAdapter;
import acr18as.sheffield.ac.uk.takemeback.model.User;
import acr18as.sheffield.ac.uk.takemeback.receivers.ARBroadcastReceiver;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private static User user;
    private PagerAdapter mFragmentAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private ARBroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Creating tabs
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Map").setIcon(R.drawable.ic_map_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText("Settings").setIcon(R.drawable.ic_settings_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setText("About").setIcon(R.drawable.ic_info_outline_white_24dp));
        tabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.White), PorterDuff.Mode.SRC_IN);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        //Apply the Adapter
        mFragmentAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager = (ViewPager) findViewById(R.id.vpager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mFragmentAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.White);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                // refreshing the MapFragment to apply changes made in SettingsFragment
                if(tab.getPosition() == 0) {
                    MapFragment.getFragment().onResume();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.Black);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Creating a new User instance and setting the value to the Singleton object
        User user = new User();
        ((UserClient)getApplicationContext()).setUser(user);
        this.user = user;

        //-----------------//

        broadcastReceiver = new ARBroadcastReceiver();
        broadcastReceiver.setMainActivity(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));

        /*
        //defining the broadcast receiver
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };

        //registering it
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));

        //start AR service
        startRecognitionService();
        */

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    //-----------------------------------------------------------------------------------------------//

    /*
    private void startRecognitionService() {
        if(!isRecognitionServiceRunning()){
            Intent serviceIntent = new Intent(this, BackgroundDetectedActivitiesService.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                this.startForegroundService(serviceIntent);
            }else{
                this.startService(serviceIntent);
            }
        }
    }

    private void stopRecognitionService() {
        Intent serviceIntent = new Intent(this, BackgroundDetectedActivitiesService.class);
        stopService(serviceIntent);
        Intent detectedARIntent = new Intent(this, DetectedActivitiesIntentService.class);
        stopService(detectedARIntent);
    }

    private boolean isRecognitionServiceRunning() {
        ActivityManager manager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("acr18as.sheffield.ac.uk.takemeback.services.BackgroundDetectedActivitiesService".equals(service.service.getClassName())) {
                Log.d(TAG, "isRecognitionServiceRunning: recognition service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isRecognitionServiceRunning: recognition service is not running.");
        return false;
    }

    //-----------------------------------------------------------------------------------------------//


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

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
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
            Toast.makeText(this, "Activity: " + label, Toast.LENGTH_SHORT).show();
        }
    }
    */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

}
