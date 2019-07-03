package acr18as.sheffield.ac.uk.takemeback.services;

import android.content.Context;

import com.google.android.gms.location.ActivityRecognitionClient;

import androidx.annotation.NonNull;

public class ActivityRecognitionService extends ActivityRecognitionClient {
    public ActivityRecognitionService(@NonNull Context context) {
        super(context);
    }
}
