package edu.uiuc.cs427app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.apache.commons.lang3.StringUtils;
import org.chromium.net.CronetEngine;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import edu.uiuc.cs427app.BuildConfig;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.cache.WeatherIdCache;
import edu.uiuc.cs427app.data.models.Location;
import edu.uiuc.cs427app.viewmodel.LocationsViewModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@AndroidEntryPoint
public class NewLocationActivity extends BaseActivity {

    private LocationsViewModel locationsViewModel;
    private EditText editTextCity;
    private TextView googleLocationDetails;
    private TextView accuweatherLocationDetails;
    private UUID loggedInUser;
    private static final String API_KEY = BuildConfig.MAPS_API_KEY;
    private Boolean isValidLocation = false;
    private String city;
    private String address;
    private String accuweatherLocationId;
    private double latitude;
    private double longitude;

    @Inject
    CronetEngine cronetEngine;

    /**
     * Creates the activity
     * @param savedInstanceState the instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_location);

        locationsViewModel = new ViewModelProvider(this).get(LocationsViewModel.class);

        loggedInUser = (UUID) getIntent().getExtras().get("user");
        editTextCity = findViewById(R.id.editTextCity);
        googleLocationDetails = findViewById(R.id.textViewLocationDetails);
        accuweatherLocationDetails = findViewById(R.id.textViewWeatherLocationDetails);
        Button buttonNewLocation = findViewById(R.id.buttonAddNewLocation);
        Button buttonValidateLocation = findViewById(R.id.buttonValidateLocation);

        buttonNewLocation.setOnClickListener(view -> addLocation());
        buttonValidateLocation.setOnClickListener(view -> validateLocation(true, null));
    }

    /**
     * Get the full location name from the address components retrieved from the Google Maps API
     * @param addressComponents the address components
     * @return the full location name as City, State, Country
     */
    private String getFullLocationName(JSONArray addressComponents) {
        String city = "";
        String state = "";
        String country = "";

        try {
            for (int i = 0; i < addressComponents.length(); i++) {
                JSONObject component = addressComponents.getJSONObject(i);
                JSONArray types = component.getJSONArray("types");
                if (types.toString().contains("locality")) {
                    city = component.getString("long_name");
                } else if (types.toString().contains("administrative_area_level_1")) {
                    state = component.getString("short_name");
                } else if (types.toString().contains("country")) {
                    country = component.getString("long_name");
                }
            }
        } catch (Exception e) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Error parsing location", Toast.LENGTH_SHORT).show();
            });
        }

        return city + ", " + state + ", " + country;
    }

    // Validate Location Method validates the location entered by the user with the Google Maps API
    // and determines the latitude, longitude, city, and address fields

    /**
     * Validates the location entered by the user with the Google Maps API and
     * determines the latitude, longitude, city, and address fields
     * @param showLocationDetails whether to show the location details in the UI
     * @param onComplete the callback to run after the validation is complete
     */
    private void validateLocation(boolean showLocationDetails, Runnable onComplete) {
        new Thread(() -> {
            isValidLocation = false;
            city = "";
            address = "";
            latitude = 0;
            longitude = 0;
            try {
                if (StringUtils.isBlank(editTextCity.getText().toString())) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Fill out all fields", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    boolean isValidGoogleLocation = validateGoogleLocation(showLocationDetails);
                    boolean isValidAccuweatherLocation = false;
                    if (isValidGoogleLocation) {
                        isValidAccuweatherLocation = validateAccuweatherLocation(showLocationDetails);
                    }
                    isValidLocation = isValidGoogleLocation && isValidAccuweatherLocation;
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (onComplete != null) {
                    runOnUiThread(onComplete);
                }
            }
        }).start();
    }

    /**
     * Validates the google maps location
     * @param showLocationDetails whether to show the weather details on the screen
     * @return whether the locatino was valid
     * @throws Exception if http error
     */
    private boolean validateGoogleLocation(boolean showLocationDetails) throws Exception {
        if (StringUtils.isBlank(editTextCity.getText().toString())) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Fill out all fields", Toast.LENGTH_SHORT).show();
            });
        } else {
            String cityName = editTextCity.getText().toString();
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + cityName + "&key=" + API_KEY;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);

                if (jsonObject.getString("status").equals("OK")) {
                    JSONObject location = jsonObject.getJSONArray("results").getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location");
                    latitude = location.getDouble("lat");
                    longitude = location.getDouble("lng");
                    city = getFullLocationName(jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components"));
                    address = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                    String s = "Google API Location is valid => \nCity: " + city + "\nAddress: " + address + "\nLatitude: " + latitude + "\nLongitude: " + longitude;
                    if (showLocationDetails) {
                        runOnUiThread(() -> {
                            googleLocationDetails.setText(s);
                        });
                    }
                    return true;
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show();
                });
            }
        }
        return false;
    }

    /**
     * Validates the accuweather location
     * @param showLocationDetails whether to show the weather details on the screen
     * @return whether the locatino was valid
     * @throws Exception if http error
     */
    private boolean validateAccuweatherLocation(boolean showLocationDetails) throws Exception {
        String url = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=" +
                BuildConfig.ACCUWEATHER_API_KEY + "&q=" + city;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseBody = response.body().string();

            Log.d("accuweather", "Response: " + responseBody);

            JsonArray convertedObject = new Gson().fromJson(responseBody, JsonArray.class);
            accuweatherLocationId = convertedObject.get(0).getAsJsonObject().get("Key").getAsString();
            String s = "AccuweatherLocation is valid => \nId: " + accuweatherLocationId;
            if (showLocationDetails) {
                runOnUiThread(() -> {
                    accuweatherLocationDetails.setText(s);
                });
            }
            return true;

        } else {
            runOnUiThread(() -> {
                Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show();
            });
        }
        return false;
    }

    /**
     * Adds the location to the database if it is valid
     */
    private void addLocation() {
        if (StringUtils.isBlank(editTextCity.getText().toString())) {
            Toast.makeText(this, "Fill out all fields", Toast.LENGTH_SHORT).show();
        } else {
            validateLocation(false, () -> {
                if (isValidLocation) {
                    Location location = new Location(
                            UUID.randomUUID(),
                            loggedInUser,
                            city,
                            latitude,
                            longitude,
                            address);
                    locationsViewModel.insertLocation(location);

                    WeatherIdCache.storeWeatherId(getApplicationContext(), location.getId().toString(), accuweatherLocationId);

                    openMainActivity();
                }
            });
        }
    }

    // Open Main Activity
    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}