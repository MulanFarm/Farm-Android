package com.example.user.demo;

import android.app.Application;

/**
 * Created by wzy on 2016/6/13.
 */
public class MyApp extends Application {
    public static MyApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
