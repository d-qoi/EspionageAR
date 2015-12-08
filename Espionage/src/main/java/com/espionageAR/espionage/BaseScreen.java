package com.espionageAR.espionage;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * This is the main for drawing the screen. Right now this can draw the basic three-button with
 * radar screen.
 *
 * What still needs to be implemented (for first release):
 *   - A gatekeeper splash screen that forces you to log in if networking isn't up.
 *   - A secondary screen for account info which has:
 *     - ID number
 *     - Alias
 *     - Score
 *     - Money
 *     - Logout button
 *   - OnCreate needs to be modified to check for a running service before starting networking
 *
 * What would be nice to fix before first release:
 *   - How do I make the OpenGL frame not draw across the entire screen height?
 */

public class BaseScreen extends FragmentActivity {

    Networking mService;
    boolean mBound = false;

    public void onCreate(Bundle savedInstanceState) {

        //Create the networking service.
        Intent networkIntent = new Intent(this, Networking.class);
        bindService(networkIntent, mConnection, Context.BIND_AUTO_CREATE);
        Toast.makeText(this, "Networking Started", Toast.LENGTH_SHORT).show();

        //This creates the layout and locks the orientation.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_screen);

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Networking.LocalBinder binder = (Networking.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }


    };

    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    //So unless we want to change the gui response, shoot and stab are done. The server handles
    //value updating, so the heartbeat function will catch a score and money change.
    public void sendShoot(View view) {
        Toast.makeText(this, "Shooting!", Toast.LENGTH_SHORT).show();

        int result = mService.onShoot (1);

        if (result==1) {
            Toast.makeText(this, "Hit!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Miss!", Toast.LENGTH_SHORT).show();
        }

    }

    public void sendStab(View view) {
        Toast.makeText(this, "Stabbing!", Toast.LENGTH_SHORT).show();

        int result = mService.onShoot (1);

        if (result==1) {
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendSearch(View view) {
        //This is going to be more complex.
    }
}
