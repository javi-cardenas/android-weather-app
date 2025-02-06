package edu.uiuc.cs427app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.UUID;

import edu.uiuc.cs427app.data.models.Location;

@Dao
public interface LocationDao {

    /**
     * Query for all locations
     * @return the list of locations in live data
     */
    @Query("SELECT * FROM locations")
    LiveData<List<Location>> getAllLocations();

    /**
     * Query for a location by its id
     * @param id the id of the location
     * @return location in live data
     */
    @Query("SELECT * FROM locations WHERE id = :id")
    LiveData<Location> getLocationById(UUID id);

    /**
     * Query for selecting locations based off of a userid
     * @param userId the id to query by
     * @return list of locations in live data format
     */
    @Query("SELECT * FROM locations WHERE user_id = :userId")
    LiveData<List<Location>> getLocationsByUserId(UUID userId);

    /**
     * Insert a location
     * @param location the location tp update
     * @return listenable for id on location
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertLocation(Location location);

    /**
     * Updating a location
     * @param location the location to update
     * @return the location key in live data
     */
    @Update
    ListenableFuture<Integer> updateLocation(Location location);

    /**
     * Deleting a location
     * @param location location to delete
     * @return the id of the location deleted
     */
    @Delete
    ListenableFuture<Integer> deleteLocation(Location location);
}
