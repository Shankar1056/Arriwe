package com.arriwe.wayndr;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Abhi1 on 26/08/15.
 */
public class GCMIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "GCMIDListenerService";

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
