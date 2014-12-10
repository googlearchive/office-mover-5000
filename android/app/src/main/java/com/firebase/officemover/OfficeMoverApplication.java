package com.firebase.officemover;

import com.firebase.client.Firebase;

/**
 * @author Jenny Tong (mimming)
 * @since 12/8/14
 *
 * Initialize Firebase with the application context. This must happen before the client is used.
 */
public class OfficeMoverApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
