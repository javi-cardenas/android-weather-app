package edu.uiuc.cs427app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.switchmaterial.SwitchMaterial;
import edu.uiuc.cs427app.R;
import edu.uiuc.cs427app.ui.utilities.Utils;

public class SettingsActivity extends BaseActivity {

    /**
     * Creates the activity
     * @param savedInstanceState the instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchMaterial themeSwitch = findViewById(R.id.themeSwitch);
        Spinner themeSpinner = findViewById(R.id.themeSelector);

        // Load saved theme mode preference (light or dark) and theme color
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.THEME_PREFS, MODE_PRIVATE);
        boolean isNightMode = sharedPreferences.getBoolean(Utils.THEME_MODE, false);
        String colorThemePreference = sharedPreferences.getString(Utils.THEME_COLOR, "Theme.Purple");

        // Set initial selection of Spinner and switch based on saved color and mode
        themeSwitch.setChecked(isNightMode);
        switch (colorThemePreference) {
            case "Theme.Teal":
                themeSpinner.setSelection(1);
                break;
            case "Theme.Orange":
                themeSpinner.setSelection(2);
                break;
            default:
                themeSpinner.setSelection(0);
                break;
        }

        // Set listener for the Switch to toggle light/dark mode
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Utils.saveThemeModePreference(buttonView.getContext(), isChecked);
            // Only recreate if the mode has actually changed
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            // update user's theme mode
            recreate(); // Recreate the activity to apply the new mode
        });

        // Set up the listener for theme selection from the Spinner
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /**
             * Assigns a color preference based on item selected
             * @param parent the parent view
             * @param view the current view
             * @param position the position in list
             * @param id the id of the item selected
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String themeName = "Theme.Purple"; // Default to Purple
                switch (position) {
                    case 0:
                        themeName = "Theme.Purple";
                        break;
                    case 1:
                        themeName = "Theme.Teal";
                        break;
                    case 2:
                        themeName = "Theme.Orange";
                        break;
                }
                // Only apply the theme if it has changed
                if (!themeName.equals(colorThemePreference)) {
                    Utils.saveThemeColorPreference(view.getContext(), themeName);
                    // update user's theme color
//                    applyColorTheme(themeName); // not needed if finishing activity below
//                    recreate(); // Recreate the activity to apply the new color theme

                    // Restart MainActivity to apply the new theme across the app
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Close SettingsActivity to ensure MainActivity is refreshed
                }
            }

            /**
             * Method for when nothing is selected
             * @param parent the parent view
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    /**
     * Apply Color Themes
     * @param themeName theme name to assign the theme to.
     */
    private void applyColorTheme(String themeName) {
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
    }
}

