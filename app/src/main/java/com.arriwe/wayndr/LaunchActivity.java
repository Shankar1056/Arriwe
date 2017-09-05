package com.arriwe.wayndr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.sancsvision.arriwe.R;

import io.fabric.sdk.android.Fabric;


public class LaunchActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final Boolean alreadyLoginStatus = prefs.getBoolean(getResources().getString(R.string.key_is_user_logged_in),false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent;
                if(!alreadyLoginStatus) {
                    mainIntent = new Intent(LaunchActivity.this,LoginActivity.class);
                    LaunchActivity.this.startActivity(mainIntent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    LaunchActivity.this.finish();
                }
                else{
                    mainIntent = new Intent(LaunchActivity.this, Eight.class);
                    LaunchActivity.this.startActivity(mainIntent);
                    LaunchActivity.this.finish();
                }

             //   overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
