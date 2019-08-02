package acr18as.sheffield.ac.uk.takemeback.view;

import acr18as.sheffield.ac.uk.takemeback.R;
import acr18as.sheffield.ac.uk.takemeback.viewmodel.VisitedLocationViewModel;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

public class StatisticsActivity extends AppCompatActivity {

    private VisitedLocationViewModel visitedLocationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.statistics_toolbar);
        toolbar.setTitle("Visited Locations Statistics");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.White));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        visitedLocationViewModel = ViewModelProviders.of(this).get(VisitedLocationViewModel.class);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView total = findViewById(R.id.total_text);
        TextView buses = findViewById(R.id.bus_stops_text);
        TextView trains = findViewById(R.id.train_stations_text);
        TextView parking = findViewById(R.id.parking_text);
        TextView street = findViewById(R.id.street_text);
        TextView error = findViewById(R.id.error_text);

        visitedLocationViewModel.getLocationsCount().observe(this, count -> {
            total.setText(count + "");
        });

        visitedLocationViewModel.getLocationsTypeCount("bus").observe(this, count -> {
            buses.setText(count + "");
        });

        visitedLocationViewModel.getLocationsTypeCount("train").observe(this, count -> {
            trains.setText(count + "");
        });

        visitedLocationViewModel.getLocationsTypeCount("parking").observe(this, count -> {
            parking.setText(count + "");
        });

        visitedLocationViewModel.getLocationsTypeCount("street").observe(this, count -> {
            street.setText(count + "");
        });

        visitedLocationViewModel.getLocationsTypeCount("error").observe(this, count -> {
            error.setText(count + "");
        });
    }
}
