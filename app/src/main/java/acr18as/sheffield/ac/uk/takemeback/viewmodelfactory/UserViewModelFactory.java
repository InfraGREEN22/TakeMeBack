package acr18as.sheffield.ac.uk.takemeback.viewmodelfactory;

import android.app.Application;
import android.content.Context;

import acr18as.sheffield.ac.uk.takemeback.viewmodel.UserViewModel;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UserViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private Context mContext;

    public UserViewModelFactory(Application application, Context context) {
        mApplication = application;
        mContext = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserViewModel(mApplication, mContext);
    }
}
