package edu.uiuc.cs427app.data.repository;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import edu.uiuc.cs427app.data.dao.LocationDao;
import edu.uiuc.cs427app.data.models.Location;

public class LocationsRepository {
    private final LocationDao locationDao;

    // Constructor
    @Inject
    public LocationsRepository(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    /**
     * Get all locations.
     * @return LiveData list of all of the locations.
     */
    public LiveData<List<Location>> getAllLocations() {
        return locationDao.getAllLocations();
    }

    /**
     * Get location based of off id.
     * @return LiveData of the location based off of id.
     */
    public LiveData<Location> getLocationById(UUID id) {
        return locationDao.getLocationById(id);
    }

    /**
     * Get list of all locations based off of userId.
     * @param userId userId of user
     * @return LiveData list of all of the locations based off of userId.
     */
    public LiveData<List<Location>> getLocationsByUserId(UUID userId) {
        return locationDao.getLocationsByUserId(userId); // Return LiveData
    }

    /**
     * Insert location data.
     * @param location  Location to be inserted.
     * @param callback call InsertCallback to check for success or error
     */
    public void insertLocation(Location location, InsertCallback callback) {
        new Thread(() -> {
            try {
                locationDao.insertLocation(location);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError("Error inserting location");
            }
        }).start();
    }

    /**
     * Update location data.
     * @param location  Location to be updated.
     * @param callback call UpdateCallback to check for success or error
     */
    public void updateLocation(Location location, UpdateCallback callback) {
        new Thread(() -> {
            try {
                locationDao.updateLocation(location);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError("Error updating location");
            }
        }).start();
    }

    /**
     * Delete location data.
     * @param location  Location to be deleted.
     * @param callback call DeleteCallback to check for success or error
     */
    public void deleteLocation(Location location, DeleteCallback callback) {
        new Thread(() -> {
            try {
                locationDao.deleteLocation(location);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError("Error deleting location");
            }
        }).start();
    }

    // Callbacks
    public interface InsertCallback {

        /**
         * Method called on success
         */
        void onSuccess();

        /**
         * Method called on error
         * @param error the error string
         */
        void onError(String error);
    }

    public interface UpdateCallback {

        /**
         * Method called on success
         */
        void onSuccess();

        /**
         * Method called on error
         * @param error the error string
         */
        void onError(String error);
    }

    public interface DeleteCallback {

        /**
         * Method called on success
         */
        void onSuccess();

        /**
         * Method called on error
         * @param error the error string
         */
        void onError(String error);
    }
}
