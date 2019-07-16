package acr18as.sheffield.ac.uk.takemeback.viewmodelfactories;

import android.app.Application;
import android.content.Context;

import com.google.maps.GeoApiContext;

import acr18as.sheffield.ac.uk.takemeback.viewmodel.RouteViewModel;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RouteViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private GeoApiContext mGeoApiContext;
    private Context mContext;

    public RouteViewModelFactory(Application application, GeoApiContext geoApiContext,
                                 Context context) {
        mApplication = application;
        mGeoApiContext = geoApiContext;
        mContext = context;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RouteViewModel(mApplication, mGeoApiContext, mContext);
    }
}
