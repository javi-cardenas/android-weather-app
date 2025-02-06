package edu.uiuc.cs427app.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import edu.uiuc.cs427app.BuildConfig;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.cache.WeatherIdCache;
import edu.uiuc.cs427app.data.models.Location;
import edu.uiuc.cs427app.http.weather.WeatherDetailsCallback;
import edu.uiuc.cs427app.viewmodel.LocationsViewModel;

@AndroidEntryPoint
public class DetailsActivity extends BaseActivity implements View.OnClickListener{

    @Inject
    CronetEngine cronetEngine;

    private LocationsViewModel locationsViewModel;

    private Location location;

    TextView textDate;
    TextView textTime;
    TextView textTemperature;
    TextView textWeather;
    TextView textHumidity;
    TextView textWindCondition;

    /**
     * Creates the activity
     * @param savedInstanceState the instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Create viewModel and grab locationId from intent
        locationsViewModel = new ViewModelProvider(this).get(LocationsViewModel.class);
        UUID locationId = UUID.fromString(getIntent().getStringExtra("location_id"));

        // Process the passed location to build the details page
        locationsViewModel.getLocationByID(locationId).observe(this, location -> {
            // Fix for redundant onCreate call generated on activity finish
            if (location == null) {
                return;
            }

            // Grab location and build existing UI strings
            this.location = location;
            String welcome = "Welcome to " + location.getCity();

            // Initializing the GUI elements
            TextView welcomeMessage = findViewById(R.id.welcomeText);

            welcomeMessage.setText(welcome);
            // Get the weather information from a Service that connects to a weather server and show the results

            Button weatherDetailsButton = findViewById(R.id.weatherDetailsButton);
            weatherDetailsButton.setOnClickListener(this);

        });

        textDate = findViewById(R.id.textDate);
        textTime = findViewById(R.id.textTime);
        textTemperature = findViewById(R.id.textTemperature);
        textWeather = findViewById(R.id.textWeather);
        textHumidity = findViewById(R.id.textHumidity);
        textWindCondition = findViewById(R.id.textWindCondition);


        Executor executor = Executors.newSingleThreadExecutor();

        String url = "http://dataservice.accuweather.com/currentconditions/v1/" +
                WeatherIdCache.getWeatherId(getApplicationContext(), locationId.toString()) + "?apikey=" +
                BuildConfig.ACCUWEATHER_API_KEY + "&details=true";

        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                url, new WeatherDetailsCallback(textDate, textTime, textTemperature, textWeather, textHumidity, textWindCondition), executor);

        UrlRequest request = requestBuilder.build();

        request.start();
    }


    /**
     * Go back to main or to the map on click.
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        //Implement this (create an Intent that goes to a new Activity, which shows the map)
        Intent intent;
        if (view.getId() == R.id.weatherDetailsButton) {
            intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("weatherCondition", textWeather.getText());
            intent.putExtra("windCondition", textWindCondition.getText());
            intent.putExtra("temperature", textTemperature.getText());
            intent.putExtra("humidity", textHumidity.getText());
            startActivity(intent);
        }
    }
}

