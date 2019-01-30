package com.android.firebasetodo;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class TodoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
