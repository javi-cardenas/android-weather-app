package edu.uiuc.cs427app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import dagger.hilt.android.AndroidEntryPoint;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.data.models.User;
import edu.uiuc.cs427app.viewmodel.UserViewModel;
import edu.uiuc.cs427app.ui.utilities.Utils;

@AndroidEntryPoint
public class SignupActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private EditText editTextUsername;
    private EditText editTextPassword;

    /**
     * Creates the activity
     * @param savedInstanceState the instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        SwitchMaterial themeSwitch = findViewById(R.id.themeSwitch);
        Spinner themeSpinner = findViewById(R.id.themeSelector);
        Button buttonSignup = findViewById(R.id.buttonSignup);

        // setup for UI selection from the user
        Utils.initThemeSelector(themeSpinner);
        Utils.initThemeMode(themeSwitch);

        buttonSignup.setOnClickListener(view -> signupUser());

        userViewModel.getSignupError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        userViewModel.getLoggedInUser().observe(this, user -> {
            if (user != null) {
                openMainActivity();
            }
        });
    }

    /**
     *     // Sign Up User method
     */
    private void signupUser() {

        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        // TODO: Handle first name, last name, and email
        User user = new User(username, password, "", "", "",
                Utils.themeColor, Utils.themeMode);
        userViewModel.signup(user);
    }

    /**
     *     // Open Main Activity
     */
    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the signup activity
    }
}
