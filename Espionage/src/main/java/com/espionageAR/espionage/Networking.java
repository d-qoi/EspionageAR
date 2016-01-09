package com.espionageAR.espionage;

/**
 * This is the main for the networking service. Right now it is an IntentService which is easier,
 * but that runs on the main thread. It would be nice to get this on its own thread, but that will
 * require moving to a regular Service class and a lot more work.
 *
 * This service is intended to take care of all the heavy numeric lifting. Because the server
 * maintains all the data for security, this will take care of heartbeating with the server
 *
 * The BaseScreen function binds to and unbinds from this networking function to do everything it
 * needs network-wise, although right now the service shuts off when BaseScreen unbinds, which is
 * no bueno. I will need to add some sort of account control screen, and log off will shut this
 * service down.
 *
 * What needs to be implemented:
 *   [ ] Server communications need to be established
 *      [ ] Server ID should be received and stored
 *      [ ] Server communication needs to be terminated on log off
 *   [ ] GPS needs to be read, JSON'd, and sent to the server each heartbeat
 *   [ ] During server heartbeat any messages from the server should be read
 *      - Unless we can set up an active listener. Then that is much better.
 *   [ ] OnStab and OnSearch need to send appropriate blips to the server
 *      [ ] w/ID?
 *   [ ] OnShoot needs to send a heading to the server
 *   [ ] OnShoot and OnStab need to receive success/failure from the server
 *   [ ] OnSearch should receive radar plotting coordinates for each found player from the server
 *      [ ] This puts minor computational load on the server, but compresses data, which is important.
 */

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import java.lang.String;


public class Networking extends IntentService {

    //Set an int to control the server heartbeat. It may be useful to change this depending on
    // if the game is active/background, potentially even allow players to change it, because a
    // faster heartbeat is not difficult or taxing for us, but it does drain device battery like a
    // mofo.
    private int tickCycle=1000;

    //Networking initialization info.
    private boolean initialized=false;
    private String idToken;
    private String uID;

    //Related heartbeat info
    private String location;

    //Gamestate info
    private int score;
    private int money;



    public Networking() {
        super("Espionage Network");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       /* Put some networking code here to instantiate network.*/
    }

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        Networking getService() {
            // Return this instance of LocalService so clients can call public methods
            return Networking.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //This is for debug safety; when things unbind the service stops.
    // We need to fix this when appropriate.
    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "Networking stopping", Toast.LENGTH_SHORT).show();
        stopSelf();
        return true;
    }

    //Below are the action functions. Here there be HTTP Requests.
    public int onShoot(int shoot) {
        tickCycle=250;
        //Right now this is a dummy function. It waits a while and then returns true.
        long endTime = System.currentTimeMillis() + 1*1000;
        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    wait(endTime - System.currentTimeMillis());
                } catch (Exception e) {
                }
            }
        }
        return 1;
    }

    public int onStab(){
        //Temporarily speed up the tic; things are happening
        tickCycle=250;
        //This dummy function is always true now.
        return 1;
    }

    public int onSearch(int search){
        //Temporarily speed up the tic; things are happening
        tickCycle=250;
        //This function needs to return a list of distance+headings.
        return 1;
    }

    //Accessory functions go here.
    //First attempts to log in, returns http code.
    //Second one returns if the login has happened.
    //Third one might do a heartbeat at some point
    //Fourth one returns the current game state

    public int onLogin(String Username,String Password,boolean newUser){
        //Initialize the code to a service unavailable error.
        int httpCode=503;

        //Add a ping here

        //Add code here to give the JSON'd username/password. Add a timeout.

        //Add code here to set up the location information. Add a timeout.

        return httpCode;
    }

    public boolean isLoggedIn(){
        return initialized;
    }

    private void heartbeat(){

        return;
    }

    public String[] readGameState(){
        String[] gameState=new String [4];
        updateGameState();

        //Separating these out for readability
        gameState[1]="0";

        return gameState;
    }

    private void updateGameState(){
        //
    }
}
