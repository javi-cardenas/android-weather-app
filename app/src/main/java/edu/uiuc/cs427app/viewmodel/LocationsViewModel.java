package edu.uiuc.cs427app.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.uiuc.cs427app.data.models.Location;
import edu.uiuc.cs427app.data.repository.LocationsRepository;

@HiltViewModel
public final class LocationsViewModel extends ViewModel {
    private final LocationsRepository locationsRepository;
    private final MutableLiveData<Location> selectedLocation = new MutableLiveData<>();
    private final MutableLiveData<List<Location>> userLocations = new MutableLiveData<>();

    // Error messages
    private final MutableLiveData<String> insertLocationError = new MutableLiveData<>();
    private final MutableLiveData<String> updateLocationError = new MutableLiveData<>();
    private final MutableLiveData<String> deleteLocationError = new MutableLiveData<>();

    /**
     * Constructor for LocationsViewModel. Injects the LocationsRepository and observes the current location in the repository.
     * @param locationsRepository The repository responsible for location-related data operations.
     */
    @Inject
    public LocationsViewModel(LocationsRepository locationsRepository) {
        this.locationsRepository = locationsRepository;
    }

    /**
     * Get all locations.
     * @return LiveData list of all of the locations.
     */
    public LiveData<List<Location>> getAllLocations() {
        return locationsRepository.getAllLocations();
    }

    /**
     * Get location based off of id.
     * @return LiveData of the location based off of the passed in id.
     */
    public LiveData<Location> getLocationByID(UUID id) {
        return locationsRepository.getLocationById(id);
    }

    /**
     * Get location based off of userId.
     * @param userId  UUID of a user.
     */
    public void fetchLocationsByUserId(UUID userId) {
        locationsRepository.getLocationsByUserId(userId).observeForever(userLocations::setValue);
    }

    // Remove the observer when the user is logged out
    public void removeLocationsObserver() {
        // TODO: Implement this method
    }

    /**
     * Insert location passed in to locationsRepository.
     * @param location  Location to be inserted into locationsRepository.
     */
    public void insertLocation(Location location) {
        locationsRepository.insertLocation(location, new LocationsRepository.InsertCallback() {

            /**
             * Method called on success
             */
            @Override
            public void onSuccess() {} // Do nothing since the user locations are already being observed

            /**
             * Method called on error
             * @param error the error string
             */
            @Override
            public void onError(String error) {
                insertLocationError.setValue(error);
            }
        });
    }

    /**
     * Update location passed in to locationsRepository.
     * @param location  Location to be updated in locationsRepository.
     */
    public void updateLocation(Location location) {
        locationsRepository.updateLocation(location, new LocationsRepository.UpdateCallback() {

            /**
             * Method called on success
             */
            @Override
            public void onSuccess() {} // Do nothing since the user locations are already being observed

            /**
             * Method called on error
             * @param error the error string
             */
            @Override
            public void onError(String error) {
                updateLocationError.setValue(error);
            }
        });
    }

    /**
     * Delete location passed in to locationsRepository.
     * @param location  Location to be deleted in locationsRepository.
     */
    public void deleteLocation(Location location) {
        locationsRepository.deleteLocation(location, new LocationsRepository.DeleteCallback() {

            /**
             * Method called on success
             */
            @Override
            public void onSuccess() {
               if (selectedLocation.getValue() != null && selectedLocation.getValue().getId().equals(location.getId())) {
                   selectedLocation.setValue(null);
               }
            }

            /**
             * Method called on error
             * @param error the error string
             */
            @Override
            public void onError(String error) {
                deleteLocationError.setValue(error);
            }
        });
    }

    /**
     * Set location passed in to selectedLocation.
     * @param location  Location to be set in selectedLocation.
     */
    public void setSelectedLocation(Location location) {
        selectedLocation.setValue(location);
    }

    /**
     * gets the selected location
     * @return location in live data
     */
    public LiveData<Location> getSelectedLocation() {
        return selectedLocation;
    }

    /**
     * gets the user locations
     * @return the list of locations in live data
     */
    public LiveData<List<Location>> getUserLocations() {
        return userLocations;
    }

    /**
     * gets the insert location error
     * @return the string as live data
     */
    public LiveData<String> getInsertLocationError() {
        return insertLocationError;
    }

    /**
     * gets the update location error
     * @return the string as live data
     */
    public LiveData<String> getUpdateLocationError() {
        return updateLocationError;
    }

    /**
     * gets the delete location error
     * @return the string as live data
     */
    public LiveData<String> getDeleteLocationError() {
        return deleteLocationError;
    }

    /**
     * clears the selected location
     */
    public void clearSelectedLocation() {
        selectedLocation.setValue(null);
    }

    /**
     * clears the insert error
     */
    public void clearInsertLocationError() {
        insertLocationError.setValue(null);
    }

    /**
     * clears the update location
     */
    public void clearUpdateLocationError() {
        updateLocationError.setValue(null);
    }

    /**
     * clears the delete location
     */
    public void clearDeleteLocationError() {
        deleteLocationError.setValue(null);
    }

    // Clear All Errors
    public void clearErrors() {
        insertLocationError.setValue(null);
        updateLocationError.setValue(null);
        deleteLocationError.setValue(null);
    }
}
