package com.espionageAR.espionage;

import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This is the main for drawing the screen. Right now this can draw the basic three-button with
 * radar screen.
 *
 * What still needs to be implemented (for first release):
 *   - The login screen needs to be able to set a new account
 *   - The radar does not draw right now
 *   - Need to check with networking to make sure nothing has happened.
 *   - Search needs to get finished
 *   - A secondary screen for account info via left swipe which has:
 *     - ID number
 *     - Alias
 *     - Score
 *     - Money
 *     - Logout button
 *   - Check the login status when actions happen, maybe?
 *   - A death lockout screen with log out option
 *
 * Extra features for later:
 *   - Saving login info securely for re-entry
 *   - Similarly, a way to save settings
 *   - Add a way to speed up the heartbeat (server needs to have a cutoff to prevent DOS attacks)
 */

public class BaseScreen extends FragmentActivity {

    Networking mService;
    boolean mBound = false;
    //Set the death timer in seconds
    int DeathTimer=0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if networking exists, create or bind
        if(isMyServiceRunning(Networking.class))
        {
            //This is where the magic happens to find or create. In this case the current intent is found
            Intent networkIntent = new Intent(this, Networking.class);
            bindService(networkIntent, mConnection, Context.BIND_AUTO_CREATE);
            Toast.makeText(this, "Networking Bound", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //In this case a new intent is created.
            Intent networkIntent = new Intent(this, Networking.class);
            bindService(networkIntent, mConnection, Context.BIND_AUTO_CREATE);
            Toast.makeText(this, "Networking Started", Toast.LENGTH_SHORT).show();
        }
        //Set screen orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Run the login screen if necessary, else start main screen
        if (mService.isLoggedIn())
            setContentView(R.layout.base_screen);
        else
            setContentView(R.layout.login_screen);
    }

    //Handle the login event
    public void sendLogIn(View view) {
        //Get the text entered
        String alias = ((EditText)findViewById(R.id.alias)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        boolean newAccount=false;

        //Digest the password using SHA256, then salt and rehash. Need to fix the DigestUtils problem.
        String digest = DigestUtils.sha256Hex(password);
        String salty = DigestUtils.sha256Hex(digest + alias);

        //Send it to the server:
        int result = mService.onLogIn(alias, salty, newAccount);
    }

    //Class to check if service exists:
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //Ensure appropriate behavior for service binding.
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
        // Unbind from the networking service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    //Below this comment lies the search, shoot, and stab functions.
    //Stab and shoot are done. Search requires some graphics interactions.
    public void sendShoot(View view) {
        //Toast.makeText(this, "Shooting!", Toast.LENGTH_SHORT).show();

        int result = mService.onShoot();

        //Check result. If a valid id is returned, tell the player who they hit.
        if (result==0) {
            Toast.makeText(this, "Miss!", Toast.LENGTH_SHORT).show();
        }
        else if(result<100000000||result>=1000000000){
            Toast.makeText(this, "Invalid ID returned. :(", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Hit player "+Integer.toString(result),Toast.LENGTH_SHORT).show();
        }


    }

    public void sendStab(View view) {
        //Toast.makeText(this, "Stabbing!", Toast.LENGTH_SHORT).show();

        int result = mService.onStab ();

        //Check result. If a valid id is returned, tell the player who they hit.
        if (result==0) {
            Toast.makeText(this, "Miss!", Toast.LENGTH_SHORT).show();
        }
        else if(result<100000000||result>=1000000000){
            Toast.makeText(this, "Invalid ID returned. :(", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Hit player "+Integer.toString(result),Toast.LENGTH_SHORT).show();
        }

        if (result==1) {
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendSearch(View view) {
        //Do the networking
        long[] result = mService.onSearch ();

        //Now pass the result into the drawing code
        if (result!=null){
            RadarFragment fragment = (RadarFragment) getSupportFragmentManager().findFragmentById(R.id.radar_fragment);
            fragment.setArcArray(result);
        }
        else{
            Toast.makeText(this, "No Spies Found!", Toast.LENGTH_SHORT).show();
        }
    }

    //This class takes care of opening and closing the new account dialogue.
    public void newAccount (View view) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        /*This is code to add a fragment. Commented out until I fix it.
        NewAccountFragment hello = new NewAccountFragment();
        fragmentTransaction.add(R.id.fragment_container, hello,);
        fragmentTransaction.commit();
        */

    }

    //This is a housekeeping class to check for info from the networking service.
    //It should be called on startup and I guess on a quick heartbeat. It's pretty small.
    //It also needs to make sure we're logged in.
    public void checkEvents() {

    }

}
