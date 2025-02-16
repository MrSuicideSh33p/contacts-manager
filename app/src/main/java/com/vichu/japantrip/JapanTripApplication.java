package com.vichu.japantrip;

import android.app.Application;
import android.content.Context;

public class JapanTripApplication extends Application {
    private static JapanTripApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
