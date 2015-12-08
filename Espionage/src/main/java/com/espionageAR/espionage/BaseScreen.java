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
        //Left here if we want to extend search functionality.
    }
}
