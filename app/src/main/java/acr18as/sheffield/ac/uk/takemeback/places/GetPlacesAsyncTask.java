package acr18as.sheffield.ac.uk.takemeback.places;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class GetPlacesAsyncTask extends AsyncTask<String, String, ArrayList<String>> {
    private static String TAG = "GetPlacesAsyncTask";
    public String placeType = null;
    public boolean isFound = false;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        ArrayList<String> placesArrayList = new ArrayList<>();
        try {
            URL url = new URL(strings[0]);
            HttpHandler httpHandler = new HttpHandler(url);
            String result = httpHandler.getURLResponse();
            JSONObject mainResponse;
            JSONArray jsonArray;
            mainResponse = new JSONObject(result);
            jsonArray = mainResponse.getJSONArray("results");

            if(jsonArray.length() > 0) {
                for(int i = 0; i<jsonArray.length(); i++) {
                    JSONObject placeJson = jsonArray.getJSONObject(i);
                    String placeName = placeJson.getString("name");
                    String lat = placeJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
                    String lng = placeJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
                    String type = placeJson.getString("types");

                    if(type.contains("bus_station")) {
                        placeType = "bus";
                        isFound = true;
                    }
                    else if(type.contains("train_station")) {
                        placeType = "train";
                        isFound = true;
                    }
                    else if(type.contains("parking")) {
                        placeType = "parking";
                        isFound = true;
                    }

                    placesArrayList.add(placeName);
                    Log.d(TAG, "Adding a place to the array: " + placeName + ", " + lat + ", " + lng + ", " + placeType);
                }
            }
            else
                Log.d(TAG, "There are no nearby locations of such type.");
        }
        catch(Exception e) {
            isFound = true;
            placeType = "error";
            e.printStackTrace();
        }
        return placesArrayList;
    }


    public String getPlaceType() {
        return this.placeType;
    }

    public boolean isFound() {
        return isFound;
    }
}
