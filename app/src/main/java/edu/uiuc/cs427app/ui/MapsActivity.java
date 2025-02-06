package edu.uiuc.cs427app.ui;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.data.models.Location;
import edu.uiuc.cs427app.databinding.ActivityMapsBinding;
import edu.uiuc.cs427app.viewmodel.LocationsViewModel;

@AndroidEntryPoint
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private LocationsViewModel locationsViewModel;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Location location;

    /**
     * Creates an instance of the activity
     * @param savedInstanceState the instance state for the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID locationId = UUID.fromString(getIntent().getStringExtra("location_id"));

        locationsViewModel = new ViewModelProvider(this).get(LocationsViewModel.class);
        locationsViewModel.getLocationByID(locationId).observe(this, l -> {
            location = l;

            binding = ActivityMapsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        TextView locationInfo = findViewById(R.id.location_info);
        if (location != null) {
            String locationText = String.format("%s, Lat: %.6f, Lng: %.6f",
                    location.getCity(), location.getLatitude(), location.getLongitude());
            locationInfo.setText(locationText);
            // Add a marker in selected location and move the camera
            LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(locationLatLng).title(location.getCity()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 10));
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }
}