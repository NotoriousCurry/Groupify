package com.tssssa.sgaheer.groupify;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Initialise application with firebase backend
 *
 * @author Samar
 * @26/4/16
 */

public class GroupifyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
