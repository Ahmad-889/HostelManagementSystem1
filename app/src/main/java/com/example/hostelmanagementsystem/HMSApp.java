package com.example.hostelmanagementsystem;

import android.app.Application;

import com.example.hostelmanagementsystem.data.FirebaseManager;

public class HMSApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseManager.init();
    }
}
