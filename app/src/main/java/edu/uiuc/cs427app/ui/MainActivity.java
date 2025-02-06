package edu.uiuc.cs427app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import java.util.List;
import dagger.hilt.android.AndroidEntryPoint;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.data.models.Location;
import edu.uiuc.cs427app.data.models.User;
import edu.uiuc.cs427app.ui.utilities.Utils;
import edu.uiuc.cs427app.viewmodel.LocationsViewModel;
import edu.uiuc.cs427app.viewmodel.UserViewModel;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

@AndroidEntryPoint
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private UserViewModel userViewModel;
    private LocationsViewModel locationsViewModel;
    private User loggedInUser;
    private ListView listViewLocations;
    private List<Location> locationList = new ArrayList<>();
    private LocationAdapter locationAdapter;
    private List<String> locationNames = new ArrayList<>();

    /**
     * Creates the activity
     * @param savedInstanceState the instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ListView
        listViewLocations = findViewById(R.id.listViewLocations);
        locationAdapter = new LocationAdapter(this,locationList);
        listViewLocations.setAdapter(locationAdapter);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        locationsViewModel = new ViewModelProvider(this).get(LocationsViewModel.class);

        userViewModel.getLoggedInUser().observe(this, user -> {
            if (user == null) {
                openLoginActivity(); // User is not logged in
                finish(); // Close the current activity to prevent the user from coming back to the main activity
            } else {
                loggedInUser = user;
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(actionBar.getTitle() + "-" + loggedInUser.getUsername());
                }
                locationsViewModel.fetchLocationsByUserId(loggedInUser.getId());
                locationsViewModel.getUserLocations().observe(this, ele -> {
                    Log.println(Log.INFO, "app", "List of locations: " + ele);
                });

                // Observe location updates and update UI
                locationsViewModel.getUserLocations().observe(this, locations -> {
                    locationList.clear();
                    locationList.addAll(locations);
                    locationNames.clear();
                    for (Location location : locations) {
                        locationNames.add(location.getCity()); // Get city name for each location
                    }
                    locationAdapter.notifyDataSetChanged();
                });

            }
        });

        // Set up click listener for each item to show details
        listViewLocations.setOnItemClickListener((parent, view, position, id) -> {
            Location selectedLocation = locationList.get(position);
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra("location_id", selectedLocation.getId());
            startActivity(intent);
        });

        // Set up the UI components
        setupUI();
    }

    /**
     * // Setup UI of App
     */
    private void setupUI() {
        // Existing code for setting up buttons and other UI components
//        Button buttonChampaign = findViewById(R.id.buttonChampaign);
//        Button buttonChicago = findViewById(R.id.buttonChicago);
//        Button buttonLA = findViewById(R.id.buttonLA);
        Button buttonNew = findViewById(R.id.buttonAddLocation);
        Button buttonSettings = findViewById(R.id.buttonSettings);
        Button buttonLogout = findViewById(R.id.buttonLogout);

//        buttonChampaign.setOnClickListener(this);
//        buttonChicago.setOnClickListener(this);
//        buttonLA.setOnClickListener(this);
        buttonNew.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
    }

    // Open Login Activity
    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Ability to add a location, go to settings, or logout.
     * @param view the app view
     */
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.buttonAddLocation:
                intent = new Intent(this, NewLocationActivity.class);
                intent.putExtra("user", loggedInUser.getId());
                startActivity(intent);
                break;
            case R.id.buttonSettings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonLogout:
                userViewModel.logout(); // Call the logout method from ViewModel

                // default to light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Utils.saveThemeModePreference(this, false);

                // Clear other user data
                SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                openLoginActivity(); // Open the login activity
                finish(); // Close the main activity
                break;
        }
    }


    private class LocationAdapter extends ArrayAdapter<Location> {

        /**
         *         // constructor
         * @param context context
         * @param locations locations
         */
        public LocationAdapter(@NonNull Context context, List<Location> locations) {
            super(context, 0, locations);
        }

        /**
         * Get View
         * @param position position.
         * @param convertView View.
         * @param parent ViewGroup.
         * @return LocationsRepository.
         */
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_location, parent, false);
            }

            // Get current location
            Location location = getItem(position);

            // Set city name
            TextView textViewCity = convertView.findViewById(R.id.textViewCity);
            textViewCity.setText(location.getCity());
            textViewCity.setPadding(0, 0, 10, 20);

            // Set up the "Show Details" button
            Button buttonShowDetails = convertView.findViewById(R.id.buttonShowWeather);
            buttonShowDetails.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("location_id", location.getId().toString());
                startActivity(intent);
            });

            Button buttonShowMap = convertView.findViewById(R.id.buttonShowMap);
            buttonShowMap.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("location_id", location.getId().toString());
                startActivity(intent);
            });

            Button buttonDelete = convertView.findViewById(R.id.buttonDelete);
            buttonDelete.setOnClickListener(v -> {
                locationsViewModel.deleteLocation(location);
            });
            buttonDelete.setBackgroundColor(Color.RED);

            return convertView;
        }
    }
}
