package acr18as.sheffield.ac.uk.takemeback;

import android.app.Application;

import acr18as.sheffield.ac.uk.takemeback.model.User;

public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
