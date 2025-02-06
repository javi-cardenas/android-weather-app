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

import edu.uiuc.cs427app.data.models.User;

@Dao
public interface UserDao {

    /**
     * Query for all users
     * @return the list of users in live data
     */
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();

    /**
     * Query for users based on there ids
     * @param id the id to retrieve by
     * @return the user in live data
     */
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    LiveData<User> getUserById(UUID id);

    /**
     * Query for users based on their usernames, returning a LiveData object
     * @param username the username to retrieve by
     * @return the user in live data
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    LiveData<User> getUserByUsername(String username);

    /**
     * Query for users based on their usernames, returning a User object
     * @param username the username to query by
     * @return the user
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsernameSync(String username);

    /**
     * Inserting a user
     * @param user the user to insert
     * @return the listenable with the user id
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertUser(User user);

    /**
     * Updating a user
     * @param user the user to update
     * @return the listenable with the id
     */
    @Update
    ListenableFuture<Integer> updateUser(User user);

    /**
     * Deleting a user
     * @param user the user to delete
     * @return the listenable with the id
     */
    @Delete
    ListenableFuture<Integer> deleteUser(User user);
}
