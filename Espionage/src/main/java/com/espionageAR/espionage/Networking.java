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
 * What still needs to be implemented:
 *   - Server communications need to be established (I do not know how to network so maybe Alex?)
 *   - GPS+Compass need to be read, JSON'd (maybe?), and sent to the server each heartbeat
 *   - During server heartbeat any messages from the server should be read
 *   - OnShoot, OnStab, and OnSearch need to send messages to the server
 *   - OnShoot and OnStab need to receive success/failure from the server
 *   - OnSearch should receive distance+angle for each found player from the server
 *      - This puts minor computational load on the server, but compresses data
 */

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;


public class Networking extends IntentService {

    //Set an int to control the server heartbeat. It may be useful to change this depending on
    // if the game is active/background, potentially even allow players to change it, because a
    // faster heartbeat is not difficult or taxing for us, but it does drain device battery like a
    // mofo.
    private int tickCycle=1000;

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

    public int onStab(int stab){
        tickCycle=250;
        //This dummy function is always true now.
        return 1;
    }

    public int onSearch(int search){
        tickCycle=250;
        //This function needs to return a list of distance+headings.
        return 1;
    }
}
