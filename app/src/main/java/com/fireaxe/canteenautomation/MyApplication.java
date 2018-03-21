package com.fireaxe.canteenautomation;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by negativezer0 on 17/3/18.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
