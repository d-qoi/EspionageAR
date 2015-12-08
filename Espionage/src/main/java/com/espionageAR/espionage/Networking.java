package com.espionageAR.espionage;

/**
 * Created by Louis on 12/7/15.
 */

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;


public class Networking extends IntentService {

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
    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "Networking stopping", Toast.LENGTH_SHORT).show();
        stopSelf();
        return true;
    }

    public int onShoot(int shoot) {
        tickCycle=250;
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

    public int onSearch(int search){
        tickCycle=250;
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
}
