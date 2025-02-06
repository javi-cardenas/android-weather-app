package edu.uiuc.cs427app.cache;

import android.content.Context;
import android.content.SharedPreferences;

public class WeatherIdCache {

    private static final String WEATHER_ID_CACHE = "WEATHER_ID_CACHE";

    /**
     * gets the weather id from the cache
     * @param context the context for saving
     * @param locationId the location id to query
     * @return the string representing the weather id
     */
    public static String getWeatherId(Context context, String locationId) {
        SharedPreferences sharedPref = context.getSharedPreferences(WEATHER_ID_CACHE, Context.MODE_PRIVATE);
        return sharedPref.getString(locationId, null);
    }

    /**
     * stores the weather id in the cache
     * @param context Context
     * @param locationId locationId as String
     * @param weatherLocationId weatherLocationId as String
     */
    public static void storeWeatherId(Context context, String locationId, String weatherLocationId) {
        SharedPreferences sharedPref = context.getSharedPreferences(WEATHER_ID_CACHE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(locationId, weatherLocationId);
        editor.apply();
    }

}
