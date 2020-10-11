package com.example.regischarles.locationbased;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static MyApplication myApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();

        myApplication = this;


    }


    public static MyApplication getInstance() {
        return myApplication;
    }

    public static Context getApplicationConext() {
        return myApplication.getApplicationContext();
    }

}