package edu.uiuc.cs427app.ui.utilities;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class Utils {

    public static final String THEME_PREFS = "themePrefs";
    public static final String THEME_COLOR = "themeColor";
    public static final String THEME_MODE = "themeMode";
    public static String themeColor;
    public static String themeMode;

    /**
     * Set a UI spinner for the selection
     * @param themeSpinner the type of spinner
     */
    public static void initThemeSelector(Spinner themeSpinner) {
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
                saveThemeColorPreference(view.getContext(), themeName);
                themeColor = themeName;
            }

            /**
             * Method to handle no selection
             * @param parent reference to parent
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    /**
     * Assign Dark or Light Mode based off of Switch
     * @param themeSwitch the switch material for UI changes
     */
    public static void initThemeMode(SwitchMaterial themeSwitch) {
        // Set listener for the Switch to toggle light/dark mode
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveThemeModePreference(buttonView.getContext(), isChecked);
            if (isChecked) themeMode = "Dark";
            else themeMode = "Light";
        });
    }

    /**
     * Save Chosen Color Preference
     * @param context the app context
     * @param themeName the new color theme
     */
    public static void saveThemeColorPreference(Context context, String themeName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(THEME_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(THEME_COLOR, themeName);
        editor.apply();
    }

    /**
     * Save Chosen Theme Preference
     * @param context the app context
     * @param isNightMode whehter we are using night mode
     */
    public static void saveThemeModePreference(Context context, boolean isNightMode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(THEME_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(THEME_MODE, isNightMode);
        editor.apply();
    }
}
