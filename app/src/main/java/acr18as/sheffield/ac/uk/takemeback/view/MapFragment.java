package acr18as.sheffield.ac.uk.takemeback.view;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import acr18as.sheffield.ac.uk.takemeback.R;
import acr18as.sheffield.ac.uk.takemeback.UserClient;
import acr18as.sheffield.ac.uk.takemeback.model.User;
import acr18as.sheffield.ac.uk.takemeback.places.GetPlacesAsyncTask;
import acr18as.sheffield.ac.uk.takemeback.receiver.ARBroadcastReceiver;
import acr18as.sheffield.ac.uk.takemeback.roomdb.SavedLocation;
import acr18as.sheffield.ac.uk.takemeback.roomdb.VisitedLocation;
import acr18as.sheffield.ac.uk.takemeback.service.LocationService;
import acr18as.sheffield.ac.uk.takemeback.viewmodel.SavedLocationViewModel;
import acr18as.sheffield.ac.uk.takemeback.viewmodel.UserViewModel;
import acr18as.sheffield.ac.uk.takemeback.viewmodel.VisitedLocationViewModel;
import acr18as.sheffield.ac.uk.takemeback.viewmodelfactory.UserViewModelFactory;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MapFragment";

    private static MapFragment fragment;
    public static void setFragment(MapFragment fragment) {
        MapFragment.fragment = fragment;
    }
    public static MapFragment getFragment() { return fragment; }

    private Location mSavedLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    private SharedPreferences sharedPreferences;

    private MapView mMapView;
    //TODO: Make it private when finished with testing
    private GoogleMap googleMap;
    private Marker savedLocationMarker;
    private SavedLocation lastSavedLocation;
    public ArrayList<Marker> visitedMarkers;
    private GeoApiContext mGeoApiContext = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String placeType = null;

    //UserViewModel
    private UserViewModel userViewModel;
    private VisitedLocationViewModel visitedLocationViewModel;
    private SavedLocationViewModel savedLocationViewModel;

    /**
     * Two main buttons of the Map fragment.
     */
    private Button mSaveButton;
    private Button mFindRouteButton;
    private FloatingActionButton fabCurrentPosition;
    private FloatingActionButton fabDeleteDestination;

    private OnFragmentInteractionListener mListener;

    // TODO: Soon replace it with UserViewModel
    private static User user;

    public MapFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setFragment(this);
        visitedMarkers = new ArrayList<>();
        //user = ((UserClient)getActivity().getApplicationContext()).getUser();

        UserViewModelFactory factory = new UserViewModelFactory(getActivity().getApplication(), getContext());
        userViewModel = ViewModelProviders.of(this.getActivity(), factory).get(UserViewModel.class);
        visitedLocationViewModel = ViewModelProviders.of(this.getActivity()).get(VisitedLocationViewModel.class);
        savedLocationViewModel = ViewModelProviders.of((this.getActivity())).get(SavedLocationViewModel.class);
        lastSavedLocation = null;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        Log.d(TAG, "Fragment has been created.");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        Log.d(TAG, "View has been created.");
        initGoogleMap(savedInstanceState, rootView);

        // setting click event for a Save Location button
        mSaveButton = rootView.findViewById(R.id.save_location_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userViewModel.getUserLocation().observe(getActivity(), location -> {
                    if(location != null) {
                        saveCurrentLocation(); //
                        //userViewModel.setUserDestinationLocation(location);
                    }
                    else
                        Toast.makeText(getContext(), "Something has gone wrong with saving... Cannot detect " +
                                "your current location.", Toast.LENGTH_SHORT).show();
                });
            }
        });

        // setting click event for a Find A Route button
        mFindRouteButton = rootView.findViewById(R.id.create_route_button);
        mFindRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //userViewModel.getUserDestinationLocation().observe(getActivity(), location -> {
                    if(lastSavedLocation == null) {
                        Toast.makeText(getContext(), "Cannot calculate a route because there is no destination" +
                                " point.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(getActivity(), RouteActivity.class);
                        startActivity(intent);
                    }
                //});
            }
        });

        // setting click event for a Delete VisitedLocation button
        fabDeleteDestination = rootView.findViewById(R.id.fab_delete_destination);
        fabDeleteDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //userViewModel.getUserDestinationLocation().observe(getActivity(), location -> {
                    if(lastSavedLocation != null)
                        requestDestinationDelete();
                    else
                        Toast.makeText(getContext(), "There is no destination point to delete!", Toast.LENGTH_SHORT).show();
                //});
            }
        });

        // setting click event for a floating action button to move to a current location
        fabCurrentPosition = rootView.findViewById(R.id.fab_current_position);
        fabCurrentPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userViewModel.getUserLocation().observe(getActivity(), location -> {
                    if(location != null) {
                        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                        moveToCurrentLocation(current);
                    }
                    else
                        Toast.makeText(getContext(), "Cannot detect your current location.", Toast.LENGTH_SHORT).show();
                });
            }
        });

        if(sharedPreferences.getBoolean("automode_switch", true))
            hideSaveButton();
        else
            showSaveButton();

        if(sharedPreferences.getBoolean("history_visibility", true))
            showMarkers();
        else
            hideMarkers();

        return rootView;
    }



    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // getting the device location for the first time and moving camera to that location
        getInitialDeviceLocation();

        // starting the LocationService
        startLocationService();


        // is not observed after clicking on Save Location button... why?
        userViewModel.getUserDestinationLocation().observe(getActivity(), destination -> {
            if(destination != null) {
                // if there is already one marker, remove it
                if (savedLocationMarker != null) {
                    savedLocationMarker.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("Your Destination");
                LatLng latLng = new LatLng(destination.getLatitude(), destination.getLongitude());
                markerOptions.position(latLng);
                savedLocationMarker = googleMap.addMarker(markerOptions);
                Toast.makeText(getContext(), "Your current location has been saved at Lat: " + mSavedLocation.getLatitude()
                        + " Long: " + mSavedLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        });

        visitedLocationViewModel.getAllLocations().observe(getActivity(), new Observer<List<VisitedLocation>>() {
            @Override
            public void onChanged(List<VisitedLocation> locations) {
                if(!locations.isEmpty()) {
                    if(!visitedMarkers.isEmpty())
                        removeMarkers();
                    for(VisitedLocation loc : locations) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title("You visited this location on " + loc.getTimestamp());
                        LatLng latLng = new LatLng(loc.getLat(), loc.getLon());
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        markerOptions.position(latLng);
                        Marker marker = googleMap.addMarker(markerOptions);
                        visitedMarkers.add(marker);
                    }
                }
                else {
                    if(!visitedMarkers.isEmpty()) {
                        removeMarkers();
                        visitedMarkers.clear();
                    }
                }
            }
        });


        // update the Saved Location according to LiveData
        savedLocationViewModel.getLastSavedLocation().observe(getActivity(), new Observer<SavedLocation>() {
            @Override
            public void onChanged(SavedLocation savedLocation) {
                lastSavedLocation = savedLocation;
                if(lastSavedLocation != null) {
                    if (savedLocationMarker != null) {
                        savedLocationMarker.remove();
                    }
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title("Your Destination Point");
                    LatLng latLng = new LatLng(lastSavedLocation.getLat(), lastSavedLocation.getLon());
                    markerOptions.position(latLng);
                    savedLocationMarker = googleMap.addMarker(markerOptions);
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            userViewModel.getUserLocation().observe(getActivity(), location -> {
                if(location != null) {
                    LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                    moveToCurrentLocation(currentLoc);
                }
            });
        }
        catch (NullPointerException e) {
            Log.e(TAG, "Cannot detect the current location");
        }

        if(sharedPreferences.getBoolean("automode_switch", true))
            hideSaveButton();
        else
            showSaveButton();

        if(sharedPreferences.getBoolean("history_visibility", true))
            showMarkers();
        else
            hideMarkers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * moving a camera to the current user location
     * @param currentLocation
     */
    private void moveToCurrentLocation(LatLng currentLocation)
    {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

    }

    /**
     * Method called only once when the Fragment is created; retrieves the device's current location
     * and moving a camera to that point. Location updates are removed on successful location obtaining.
     */
    private void getInitialDeviceLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(1000);
        mLocationRequestHighAccuracy.setFastestInterval(1000);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                Log.d(TAG, "onLocationResult: got location result.");

                Location location = locationResult.getLastLocation();

                if (location != null) {
                    super.onLocationResult(locationResult);
                    LatLng device = new LatLng(location.getLatitude(), location.getLongitude());
                    moveToCurrentLocation(device);
                    mFusedLocationClient.removeLocationUpdates(this);
                    Log.d(TAG, "initialLocationRequest: location updates stopped");
                }
            }
        }, Looper.myLooper());
    }

    /**
     * Private method for MANUAL saving a final destination location and set a marker on it
     */
    public void saveCurrentLocation() {
        try {
            userViewModel.getUserLocation().observe(getActivity(), location -> {

                userViewModel.setUserDestinationLocation(location);

                //---- Looking for nearby places of different types ----//

                getNearbyPlaces(location);

                SavedLocation savedLocation = new SavedLocation(location.getLatitude(), location.getLongitude(), placeType);
                try {
                    savedLocationViewModel.insert(savedLocation);
                    Toast.makeText(getContext(), "Your current location has been saved at Lat: " + savedLocation.getLat()
                            + " Long: " + savedLocation.getLon(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(getContext(), "Sorry, but the location cannot be saved...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });

        }
        catch (Exception e) {
            Toast.makeText(getContext(), "Something has gone wrong with saving...", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * Method for hiding "Save Current Location" button
     */
    private void hideSaveButton() {
        //mSaveButton.setVisibility(View.GONE);
        //TODO: Find a way to show it without being warped
        mSaveButton.setEnabled(false);
    }

    /**
     * Method for showing "Save Current Location" button
     */
    private void showSaveButton() {
        //mSaveButton.setVisibility(View.VISIBLE);
        //TODO: Find a way to show it without being warped
        mSaveButton.setEnabled(true);
    }

    /**
     * Start updating the current user location
     */
    private void startLocationService() {
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(getActivity(), LocationService.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                getActivity().startForegroundService(serviceIntent);
            }else{
                getActivity().startService(serviceIntent);
            }
        }
    }

    /**
     * Check if the LocationService is already running
     */
    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager)getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("acr18as.sheffield.ac.uk.takemeback.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    /**
     * Initialising Google Maps for a MapView
     * @param savedInstanceState
     * @param rootView
     */
    private void initGoogleMap(Bundle savedInstanceState, View rootView) {
        mMapView = (MapView) rootView.findViewById(R.id.main_map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
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
     * Asking a user to delete the destination point and removing a marker from the map
     */
    private void requestDestinationDelete() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete the saved destination point?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if(savedLocationMarker != null) {
                            try {
                                savedLocationMarker.remove();
                                userViewModel.setUserDestinationLocation(null);
                                savedLocationViewModel.deleteAllLocations();
                                try {
                                    if (ARBroadcastReceiver.STATE.equals("DETECTED")) {
                                        ARBroadcastReceiver.STATE = "UNDETECTED";
                                        ARBroadcastReceiver.isSaved = false;
                                    }
                                }
                                catch (NullPointerException e) {
                                    Log.d(TAG, "Null object reference");
                                }
                                Toast.makeText(getContext(), "The destination point has been successfully deleted!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Deleted the saved location marker and position");
                            }
                            catch (Exception e) {
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Saved location delete error: " + e.getMessage());
                                e.printStackTrace();
                            }
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

    private void removeMarkers() {
        for (Marker m : visitedMarkers) {
            m.remove();
        }
    }

    private void hideMarkers() {
        if(!visitedMarkers.isEmpty()) {
            for (Marker m : visitedMarkers) {
                m.setVisible(false);
            }
        }
    }

    private void showMarkers() {
        if(!visitedMarkers.isEmpty()) {
            for (Marker m : visitedMarkers) {
                m.setVisible(true);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void getNearbyPlaces(Location location) {
        String[] types = {"bus_station", "train_station", "parking"};
        try {
            for (String type : types) {
                GetPlacesAsyncTask getPlacesTask = new GetPlacesAsyncTask();
                String url = getResources().getString(R.string.nearestplacessearch) +
                        "location=" + location.getLatitude() + "," + location.getLongitude() + "&radius=10&type=" + type +
                        "&key=" + getResources().getString(R.string.google_maps_key);
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
