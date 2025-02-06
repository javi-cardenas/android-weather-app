package edu.uiuc.cs427app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.ui.utilities.Utils;
import edu.uiuc.cs427app.viewmodel.UserViewModel;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_login);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        TextView textViewSignup = findViewById(R.id.textViewSignup);

        buttonLogin.setOnClickListener(view -> loginUser());
        textViewSignup.setOnClickListener(view -> openSignupActivity());

        userViewModel.getLoginError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        userViewModel.getLoggedInUser().observe(this, user -> {
            if (user != null) {
                Utils.saveThemeColorPreference(this, user.getThemeColor());
                String themeMode = user.getThemeMode();
                if (themeMode == null) themeMode = "Light";
                Utils.saveThemeModePreference(this, themeMode.equals("Dark"));
                openMainActivity();
            }
        });
    }

    /**
     *     // Pass login information to UserViewModel
     */
    private void loginUser() {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        userViewModel.login(username, password);
    }

    /**
     *      // Start Signup Activity
     */
    private void openSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    /**
     *     // Start Main Activity
     */
    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the login activity
    }
}
