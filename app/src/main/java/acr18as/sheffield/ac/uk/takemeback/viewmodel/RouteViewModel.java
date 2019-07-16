package acr18as.sheffield.ac.uk.takemeback.viewmodel;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;

import java.util.List;

import acr18as.sheffield.ac.uk.takemeback.repository.RouteRepository;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class RouteViewModel extends AndroidViewModel {

    public RouteRepository routeRepository;

    public RouteViewModel(@NonNull Application application, GeoApiContext geoApiContext, Context context) {
        super(application);
        routeRepository = new RouteRepository(geoApiContext, context);
    }

    public LiveData<DirectionsResult> calculateDirections() {
        return routeRepository.calculateDirections();
    }

    public LiveData<List<LatLng>> getResultingPath(final DirectionsResult result) {
        return routeRepository.getResultingPath(result);
    }

}