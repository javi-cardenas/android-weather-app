package edu.uiuc.cs427app.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

import edu.uiuc.cs427app.data.dao.UserDao;
import edu.uiuc.cs427app.data.models.User;

public class UserRepository {
    private final UserDao userDao;
    private final SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USER_ID = "user_id";
    private final MutableLiveData<User> loggedInUser = new MutableLiveData<>();

    /**
     * Constructor
     * @param userDao UserDao
     * @param context Context
     */
    @Inject
    public UserRepository(UserDao userDao, Context context) {
        this.userDao = userDao;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadCurrentUser();
    }

    /**
     * load current user
     */
    private void loadCurrentUser() {
        String userId = sharedPreferences.getString(KEY_USER_ID, null);
        if (userId != null) {
            userDao.getUserById(UUID.fromString(userId)).observeForever(loggedInUser::setValue);
        } else {
            loggedInUser.setValue(null);
        }
    }

    /**
     * Get current user.
     * @return LiveData of current user.
     */
    public LiveData<User> getCurrentUser() {
        return loggedInUser;
    }

    /**
     * Get all locations.
     * @param username username of user.
     * @param password password of user.
     * @param callback LoginCallback.
     */
    public void login(String username, String password, LoginCallback callback) {
        new Thread(() -> {
            User user = userDao.getUserByUsernameSync(username);
            if (user != null && checkPassword(password, user.getPassword())) {
                callback.onSuccess(user);
                saveUserId(user.getId().toString());
                loggedInUser.postValue(user);
            } else {
                callback.onError("Invalid username or password");
            }
        }).start();
    }

    /**
     * Signup method.
     * @param user User to be created.
     * @param callback SignupCallback.
     */
    public void signup(User user, SignupCallback callback) {
        new Thread(() -> {
            User existingUser = userDao.getUserByUsernameSync(user.getUsername());

            if (existingUser == null) {
                // Username does not exist, proceed to insert the new user
                user.setPassword(hashPassword(user.getPassword())); // Hash password before saving
                userDao.insertUser(user);
                saveUserId(user.getId().toString());
                loggedInUser.postValue(user);
                callback.onSuccess(user);
            } else {
                // Username already exists
                callback.onError("Username already exists");
            }
        }).start();
    }

    /**
     * logs user out
     */
    public void logout() {
        sharedPreferences.edit().remove(KEY_USER_ID).apply();
        loggedInUser.setValue(null); // Update logged in user
    }

    /**
     * Delete user.
     * @param user User to be deleted.
     * @param callback DeleteCallback.
     */
    public void deleteUser(User user, DeleteCallback callback) {
        new Thread(() -> {
            try {
                userDao.deleteUser(user);
                logout();
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError("Error deleting user");
            }
        }).start();
    }

    /**
     * Update user.
     * @param user User to be updated.
     * @param callback UpdateCallback.
     */
    public void updateUser(User user, UpdateCallback callback) {
        new Thread(() -> {
            try {
                userDao.updateUser(user);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError("Error updating user");
            }
        }).start();
    }

    /**
     * Save UserId.
     * @param userId UserId to be saved.
     */
    private void saveUserId(String userId) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply();
    }

    /**
     * Salt and hash the password before saving to the database
     * @param password password to be salted and hashed.
     * @return String of hashed password
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Check the hashed password.
     * @param password password to be checked.
     * @param hashed hashed version of password.
     * @return Boolean on checking the hashed and original password.
     */
    private boolean checkPassword(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }

    // Callbacks
    public interface LoginCallback {

        /**
         * Method called on success
         */
        void onSuccess(User user);

        /**
         * Method called on error
         * @param error the error string
         */
        void onError(String error);
    }

    public interface SignupCallback {

        /**
         * Method called on success
         */
        void onSuccess(User user);

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
