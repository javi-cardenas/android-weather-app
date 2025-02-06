package edu.uiuc.cs427app;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class CS427Application extends Application {

    /**
     * creates the application
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
