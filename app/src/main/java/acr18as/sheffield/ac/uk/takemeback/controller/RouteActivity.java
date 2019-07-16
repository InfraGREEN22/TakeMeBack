package acr18as.sheffield.ac.uk.takemeback.controller;

import acr18as.sheffield.ac.uk.takemeback.R;
import acr18as.sheffield.ac.uk.takemeback.UserClient;
import acr18as.sheffield.ac.uk.takemeback.model.User;
import acr18as.sheffield.ac.uk.takemeback.viewmodel.RouteViewModel;
import acr18as.sheffield.ac.uk.takemeback.viewmodelfactories.RouteViewModelFactory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
//import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "RouteActivity";

    private MapView mMapView;
    private GoogleMap googleMap;
    private Marker startMarker;
    private Marker endMarker;
    private GeoApiContext mGeoApiContext = null;
    private RouteViewModel viewModel;

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
        end = user.getDestination().getDestinationPoint();

        // instantiating GoogleApiContext object which is used for calculating directions
        if(mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
        }

        //ViewModel
        RouteViewModelFactory factory = new RouteViewModelFactory(this.getApplication(), mGeoApiContext, this);
        viewModel = ViewModelProviders.of(this, factory).get(RouteViewModel.class);

        cancelButton = findViewById(R.id.route_cancel_button);
        navigateButton = findViewById(R.id.route_navigate_button);

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        setMarkers();
        viewModel.calculateDirections().observe(this, directionsResult -> {

            viewModel.getResultingPath(directionsResult).observe(this, result -> {

                Polyline polyline = googleMap.addPolyline(new PolylineOptions().addAll(result));
                polyline.setColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
                polyline.setClickable(true);
            });
        });
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

        LatLng endLatLng = new LatLng(end.getLatitude(), end.getLongitude());
        //LatLng endLatLng = new LatLng(53.380884, -1.480858);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(endLatLng);
        markerOptions.title("Your Destination");
        endMarker = googleMap.addMarker(markerOptions);
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
                        String latitude = String.valueOf(endMarker.getPosition().latitude);
                        String longitude = String.valueOf(endMarker.getPosition().longitude);

                        Uri gmmIntentUri = null;
                        if(SettingsFragment.DIRECTIONS_MODE == 0)
                            gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=w");
                        else
                            gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=d");

                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

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

    /////////////////////////////////////////////////////////////
    // The below method is for TESTING PURPOSES ONLY!!!!!
    /////////////////////////////////////////////////////////////

    private void setTestDestinationPoint() {
        // to prevent NullPointerException initially set the destination location as a user's current location
        user.getDestination().setDestinationPoint(user.getUserLocation());
        Location mSavedLocation = new Location(user.getDestination().getDestinationPoint());
        //setting a point in front of the Regent Court
        mSavedLocation.setLatitude(53.380884); mSavedLocation.setLongitude(-1.480858);
        user.getDestination().setDestinationPoint(mSavedLocation);
        end = user.getDestination().getDestinationPoint();
    }
}
