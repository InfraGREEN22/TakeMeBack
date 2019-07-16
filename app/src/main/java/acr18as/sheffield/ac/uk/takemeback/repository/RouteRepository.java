package acr18as.sheffield.ac.uk.takemeback.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

import acr18as.sheffield.ac.uk.takemeback.UserClient;
import acr18as.sheffield.ac.uk.takemeback.view.SettingsFragment;
import acr18as.sheffield.ac.uk.takemeback.model.User;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class RouteRepository {

    private static String TAG = "RouteRepository";

    private static RouteRepository routeRepository;
    private GeoApiContext mGeoApiContext = null;
    private Context context;
    private User user;

    /**
     * Route Repository constructor
     * @param mGeoApiContext
     * @param context
     */
    public RouteRepository(GeoApiContext mGeoApiContext, Context context) {
        this.mGeoApiContext = mGeoApiContext;
        this.context = context;
        this.user = ((UserClient)context.getApplicationContext()).getUser();
    }

    /**
     * Calculating directions
     * @return
     */
    public LiveData<DirectionsResult> calculateDirections(){
        final MutableLiveData<DirectionsResult> directionsResult = new MutableLiveData<>();
        ArrayList<DirectionsResult> arrayListOfResults = new ArrayList<>();

        Log.d(TAG, "calculateDirections: calculating directions.");

        if(user.getDestination().getDestinationPoint() == null || user.getUserLocation() == null) {
            Toast.makeText(context, "Unable to build a route back", Toast.LENGTH_SHORT).show();
            return null;
        }

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                user.getDestination().getDestinationPoint().getLatitude(),
                user.getDestination().getDestinationPoint().getLongitude()
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        // at this point, we want the most optimal route, so we ask for NO alternative routes
        directions.alternatives(false);
        if(SettingsFragment.DIRECTIONS_MODE == 0)
            directions.mode(TravelMode.WALKING);
        else
            directions.mode(TravelMode.DRIVING);
        directions.origin(
                new com.google.maps.model.LatLng(
                        user.getUserLocation().getLatitude(),
                        user.getUserLocation().getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                //addPolylinesToMap(result);
                directionsResult.postValue(result); // ВОЗМОЖНО, ОШИБКА ВОТ ТУТ
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());
            }
        });

        return directionsResult;
    }

    /**
     * Get resulting path from the Direction result for drawing polylines
     * @param result
     */
    public LiveData<List<com.google.android.gms.maps.model.LatLng>> getResultingPath(final DirectionsResult result){

        final MutableLiveData<List<com.google.android.gms.maps.model.LatLng>> resultPath = new MutableLiveData<>();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<com.google.android.gms.maps.model.LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new com.google.android.gms.maps.model.LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    resultPath.postValue(newDecodedPath);
                }
            }
        });

        return resultPath;
    }

}
