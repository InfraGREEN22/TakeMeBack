package acr18as.sheffield.ac.uk.takemeback.viewmodel;

import android.app.Application;

import acr18as.sheffield.ac.uk.takemeback.UserClient;
import acr18as.sheffield.ac.uk.takemeback.model.User;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserViewModel extends AndroidViewModel {

    private MutableLiveData<User> user;

    public UserViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<User> getUser() {
        if (user == null) {
            user = new MutableLiveData<User>();
            loadUser();
        }
        return user;
    }

    private void loadUser() {
        //user = ((UserClient)getApplication().getApplicationContext()).getUser();
    }
}
