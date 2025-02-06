package edu.uiuc.cs427app.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.ui.utilities.Utils;

public class BaseActivity extends AppCompatActivity {

    /**
     * Creates the base activity
     * @param savedInstanceState the instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load and apply the saved theme before calling super.onCreate()
        loadThemePreference();
        super.onCreate(savedInstanceState);
    }

    /**
     // Load Theme Preferences of User
     // User can select between Teal, Orange, and Purple, and also can choose a Dark Mode
     */
    private void loadThemePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.THEME_PREFS, MODE_PRIVATE);
        String themeName = sharedPreferences.getString(Utils.THEME_COLOR, "Theme.Purple");
        boolean isNightMode = sharedPreferences.getBoolean(Utils.THEME_MODE, false);

        // Apply the selected color theme
        int themeResId;
        switch (themeName) {
            case "Theme.Teal":
                themeResId = R.style.Theme_Teal;
                break;
            case "Theme.Orange":
                themeResId = R.style.Theme_Orange;
                break;
            default:
                themeResId = R.style.Theme_Purple;
                break;
        }
        setTheme(themeResId);

        // Get the current night mode setting
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Set the appropriate theme mode only if it's different from the current one
        if (isNightMode && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (!isNightMode && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
