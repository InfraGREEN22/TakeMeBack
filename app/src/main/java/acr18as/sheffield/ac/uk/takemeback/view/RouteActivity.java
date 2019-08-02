package acr18as.sheffield.ac.uk.takemeback.view;

import acr18as.sheffield.ac.uk.takemeback.R;
import acr18as.sheffield.ac.uk.takemeback.UserClient;
import acr18as.sheffield.ac.uk.takemeback.model.User;
import acr18as.sheffield.ac.uk.takemeback.receiver.ARBroadcastReceiver;
import acr18as.sheffield.ac.uk.takemeback.roomdb.SavedLocation;
import acr18as.sheffield.ac.uk.takemeback.roomdb.VisitedLocation;
import acr18as.sheffield.ac.uk.takemeback.viewmodel.RouteViewModel;
import acr18as.sheffield.ac.uk.takemeback.viewmodel.SavedLocationViewModel;
import acr18as.sheffield.ac.uk.takemeback.viewmodel.VisitedLocationViewModel;
import acr18as.sheffield.ac.uk.takemeback.viewmodelfactory.RouteViewModelFactory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;

import java.text.SimpleDateFormat;
import java.util.Date;
//import com.google.maps.model.LatLng;


public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "RouteActivity";

    private MapView mMapView;
    private GoogleMap googleMap;
    private Marker startMarker;
    private Marker endMarker;
    private GeoApiContext mGeoApiContext = null;
    private RouteViewModel routeViewModel;
    private VisitedLocationViewModel visitedLocationViewModel;
    private SavedLocationViewModel savedLocationViewModel;
    private SimpleDateFormat simpleDateFormat;
    private SharedPreferences sharedPreferences;

    private Button cancelButton;
    private Button navigateButton;

    private User user = null;
    private Location start = null;
    private Location end = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        initGoogleMap(savedInstanceState);

        // getting the user we are building a route for and setting the start and end points
        user = ((UserClient)getApplicationContext()).getUser();
        start = user.getUserLocation();
        //end = user.getDestinationLocation();

        // instantiating GoogleApiContext object which is used for calculating directions
        if(mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
        }

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //ViewModel
        RouteViewModelFactory factory = new RouteViewModelFactory(this.getApplication(), mGeoApiContext, this);
        routeViewModel = ViewModelProviders.of(this, factory).get(RouteViewModel.class);
        visitedLocationViewModel = ViewModelProviders.of(this).get(VisitedLocationViewModel.class);
        savedLocationViewModel = ViewModelProviders.of(this).get(SavedLocationViewModel.class);

        cancelButton = findViewById(R.id.route_cancel_button);
        navigateButton = findViewById(R.id.route_navigate_button);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        savedLocationViewModel.getLastSavedLocation().observe(this, new Observer<SavedLocation>() {
            @Override
            public void onChanged(SavedLocation savedLocation) {
                try {
                    Location temp = new Location("Temp");
                    temp.setLatitude(savedLocation.getLat());
                    temp.setLongitude(savedLocation.getLon());
                    temp.setTime(new Date().getTime()); //Set time as current Date
                    end = temp;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        setMarkers();

        try {
            savedLocationViewModel.getLastSavedLocation().observe(this, new Observer<SavedLocation>() {
                @Override
                public void onChanged(SavedLocation savedLocation) {
                    Location endLocation = new Location("endLocation");
                    endLocation.setLatitude(savedLocation.getLat());
                    endLocation.setLongitude(savedLocation.getLon());
                    //endLocation.setTime(new Date().getTime());

                    routeViewModel.calculateDirections(endLocation).observe(getActivity(), directionsResult -> {
                        //directionsResult.routes[0].legs[0].distance.inMeters;
                        routeViewModel.getResultingPath(directionsResult).observe(getActivity(), result -> {

                            Polyline polyline = googleMap.addPolyline(new PolylineOptions().addAll(result));
                            polyline.setColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
                            polyline.setClickable(true);
                        });
                    });
                }
            });

        }
        catch (NullPointerException e) {
            Log.e(TAG, "RouteViewModel error: ");
            e.printStackTrace();
        }
        zoomToTheRoute();
    }

    // TODO: Remove the mock destination point and make an event handler where route building query
    // TODO: is rejected if there is no destination point (possibly, make it in the MapFragment)

    /**
     * Setting the start and end markers on the map
     */
    private void setMarkers() {
        LatLng startLatLng = new LatLng(start.getLatitude(), start.getLongitude());

        // TODO: Remove the method call below when the end point saving is done
        //setTestDestinationPoint();

        MarkerOptions markerOptions = new MarkerOptions();
        savedLocationViewModel.getLastSavedLocation().observe(this, new Observer<SavedLocation>() {
            @Override
            public void onChanged(SavedLocation savedLocation) {
                LatLng endLatLng = new LatLng(savedLocation.getLat(), savedLocation.getLon());
                markerOptions.position(endLatLng);
                markerOptions.title("Your Destination");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                endMarker = googleMap.addMarker(markerOptions);
            }
        });
        //LatLng endLatLng = new LatLng(53.380884, -1.480858);
        markerOptions.position(startLatLng);
        markerOptions.title("You are here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        startMarker = googleMap.addMarker(markerOptions);
    }

    /**
     * Move a camera to the area where the route has been built
     */
    private void zoomToTheRoute() {
        LatLng location = new LatLng(start.getLatitude(), start.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    /**
     * Initialisation of MapView
     * @param savedInstanceState
     */
    private void initGoogleMap(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.route_map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();
        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

        // instantiating GoogleApiContext object which is used for calculating directions
        if(mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
        }
    }

    /**
     * Getter for the instance of current Activity
     * @return
     */
    private AppCompatActivity getActivity() {
        return this;
    }

    /**
     * Method for opening Google Maps and giving directions
     */
    private void openGoogleMaps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Start navigating to the destination point?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        double lat = savedLocationViewModel.getLastSavedLocation().getValue().getLat();
                        double lon = savedLocationViewModel.getLastSavedLocation().getValue().getLon();
                        String type = savedLocationViewModel.getLastSavedLocation().getValue().getType();
                        //String latitude = String.valueOf(endMarker.getPosition().latitude);
                        //String longitude = String.valueOf(endMarker.getPosition().longitude);

                        insertSavedLocation(lat, lon, type);

                        Uri gmmIntentUri = null;

                        String value = sharedPreferences.getString("directions_mode", "0");
                        if(value.equals("0"))
                            gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon + "&mode=w");
                        else
                            gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon + "&mode=d");

                        Log.d(TAG, "Building a route in " + value + " mode");

                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        ARBroadcastReceiver.STATE = "UNDETECTED";
                        ARBroadcastReceiver.isSaved = false;

                        try{
                            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }
                        }catch (NullPointerException e){
                            Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                            Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void insertSavedLocation(double latitude, double longitude, String type) {
        //LatLng latLng = new LatLng(latitude, longitude);
        String timestamp = simpleDateFormat.format(new Date());

        VisitedLocation location = new VisitedLocation(latitude, longitude, timestamp, type);
        visitedLocationViewModel.insert(location);
        Log.d(TAG, "Inserted a saved location into VisitedLocation DB with latitude:" + latitude + ", longitude:" + longitude +
        ", time: " + timestamp + ", type: " + type);
    }

    /////////////////////////////////////////////////////////////
    // The below method is for TESTING PURPOSES ONLY!!!!!
    /////////////////////////////////////////////////////////////

    private void setTestDestinationPoint() {
        // to prevent NullPointerException initially set the destination location as a user's current location
        user.setDestinationLocation(user.getUserLocation());
        Location mSavedLocation = new Location(user.getDestinationLocation());
        //setting a point in front of the Regent Court
        mSavedLocation.setLatitude(53.380884); mSavedLocation.setLongitude(-1.480858);
        user.setDestinationLocation(mSavedLocation);
        end = user.getDestinationLocation();
    }
}
