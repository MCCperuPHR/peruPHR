package com.app.phr.peru.peruphr_app.JAVA;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Window;
import android.view.WindowManager;

import com.app.phr.peru.peruphr_app.R;

public class Splash extends Activity {
    private static String TAG = Splash.class.getName();
    private static long SLEEP_TIME = 1;    // Sleep for some time
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Removes title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Removes notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        preferences = getSharedPreferences(PreferencePutter.PREF_FILE_NAME, Activity.MODE_PRIVATE);
        setContentView(R.layout.splash);
        // Start timer and launch main activity
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();
    }

    private class IntentLauncher extends Thread {
        @Override
        /**
         * Sleep for some time and than start new activity.
         */

        public void run() {
            try {
                // Sleeping
                Thread.sleep(SLEEP_TIME * 1000);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            // Start main activity
            boolean log_in = preferences.getBoolean(PreferencePutter.LOG_IN, false);
            if(!log_in) {
                Intent intent = new Intent(Splash.this, Login.class);
                Splash.this.startActivity(intent);
                Splash.this.finish();
            }
            else{
                Intent intent = new Intent(Splash.this, MainTab.class);
                Splash.this.startActivity(intent);
                Splash.this.finish();
            }
        }
    }
}
