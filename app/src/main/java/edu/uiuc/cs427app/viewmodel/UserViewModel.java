package edu.uiuc.cs427app.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.uiuc.cs427app.data.models.User;
import edu.uiuc.cs427app.data.repository.UserRepository;

@HiltViewModel
public final class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<User> loggedInUser = new MutableLiveData<>();

    // Error messages
    private final MutableLiveData<String> loginError = new MutableLiveData<>();
    private final MutableLiveData<String> signupError = new MutableLiveData<>();
    private final MutableLiveData<String> updateUserError = new MutableLiveData<>();
    private final MutableLiveData<String> deleteUserError = new MutableLiveData<>();

    /**
     * Constructor for UserViewModel. Injects the UserRepository and observes the current user in the repository.
     * @param userRepository The repository responsible for user-related data operations.
     */
    @Inject
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;

        // Observe the current user in the repository
        userRepository.getCurrentUser().observeForever(user -> {
            loggedInUser.setValue(user);
        });
    }

    /**
     * Starts the login process with the passed in username and password.
     * @param username The username given by the user.
     * @param password The password given by the user.
     */
    public void login(String username, String password) {
        userRepository.login(username, password, new UserRepository.LoginCallback() {

            /**
             * Method called on success
             */
            @Override
            public void onSuccess(User user) {}

            /**
             * Method called on error
             * @param error the error string
             */
            @Override
            public void onError(String error) {
                loginError.postValue(error);
            }
        });
    }

    /**
     * Starts the signup process for a new user.
     * @param user The User object containing the user's information.
     */
    public void signup(User user) {
        userRepository.signup(user, new UserRepository.SignupCallback() {

            /**
             * Method called on success
             */
            @Override
            public void onSuccess(User user) {}

            /**
             * Method called on error
             * @param error the error string
             */
            @Override
            public void onError(String error) {
                signupError.postValue(error);
            }
        });
    }

    //Logs out the currently logged-in user
    public void logout() {
        userRepository.logout();
        loggedInUser.setValue(null);
    }

    /**
     * Returns data of the currently logged-in user, allowing UI components to observe changes.
     * @return LiveData representing the logged-in user.
     */
    public LiveData<User> getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Updates the user profile in the UserRepository.
     * @param user The User object containing the updated information.
     */
    public void updateUser(User user) {
        userRepository.updateUser(user, new UserRepository.UpdateCallback() {

            /**
             * Method called on success
             */
            @Override
            public void onSuccess() {}

            /**
             * Method called on error
             * @param error the error string
             */
            @Override
            public void onError(String error) {
                updateUserError.setValue(error);
            }
        });
    }

    /**
     * Deletes the user from the repository.
     * @param user The User object for the user to be deleted.
     */
    public void deleteUser(User user) {
        userRepository.deleteUser(user, new UserRepository.DeleteCallback() {

            /**
             * Method called on success
             */
            @Override
            public void onSuccess() {}

            /**
             * Method called on error
             * @param error the error string
             */
            @Override
            public void onError(String error) {
                deleteUserError.setValue(error);
            }
        });
    }

    /**
     * gets the login error
     * @return live data of error
     */
    public LiveData<String> getLoginError() {
        return loginError;
    }

    /**
     * gets the signup error
     * @return live data of error
     */
    public LiveData<String> getSignupError() {
        return signupError;
    }

    /**
     * gets the update user error
     * @return live data of error
     */
    public LiveData<String> getUpdateUserError() {
        return updateUserError;
    }

    /**
     * gets the delete user error
     * @return live data of error
     */
    public LiveData<String> getDeleteUserError() {
        return deleteUserError;
    }


    /**
     * clears the signup error
     */
    public void clearSignupError() {
        signupError.setValue(null);
    }

    /**
     * clears the login error
     */
    public void clearLoginError() {
        loginError.setValue(null);
    }

    /**
     * clears the update user error
     */
    public void clearUpdateUserError() {
        updateUserError.setValue(null);
    }

    /**
     * clears the delete error
     */
    public void clearDeleteUserError() {
        deleteUserError.setValue(null);
    }

    //Clears all error messages
    public void clearErrors() {
        loginError.setValue(null);
        signupError.setValue(null);
        updateUserError.setValue(null);
        deleteUserError.setValue(null);
    }
}
