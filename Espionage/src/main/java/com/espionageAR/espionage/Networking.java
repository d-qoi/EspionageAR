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
 *   [ ] Respawn time update.
 *   [ ] Logout.
 */

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.lang.String;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Networking extends IntentService {

    //Set an int to control the server heartbeat. It may be useful to change this depending on
    // if the game is active/background, potentially even allow players to change it, because a
    // faster heartbeat is not difficult or taxing for us, but it does drain device battery like a
    // mofo.
    private int tickCycle=1000;

    //Networking initialization info.
    HttpClient httpClient = new DefaultHttpClient();
    private boolean initialized=false;
    private String idToken;
    private String uID;

    //Website string. At some point after beta we should probably read this from a text file
    private String websiteURL="http://webserver.EspionageAR.com/API/";

    //Networking coms strings. This may also be good to put in a ini file.
    HttpPost createUserPost = new HttpPost(websiteURL+"CreateUser");
    HttpPost locationPost = new HttpPost(websiteURL+"Location");
    HttpPut heartbeatPut = new HttpPut(websiteURL+"Heartbeat");
    HttpPut stabPut = new HttpPut(websiteURL+"Stab");
    HttpPut shootPut = new HttpPut(websiteURL+"Shoot");
    HttpPut searchPut = new HttpPut(websiteURL+"Search");
    HttpGet gameStateGet = new HttpGet(websiteURL+"GetGameState");
    HttpGet respawnTimerGet = new HttpGet(websiteURL+"RespawnTimer");
    HttpDelete logoutDelete = new HttpDelete(websiteURL+"Logout");

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
       //Try 5 ping attempts
       boolean connected=false;
       for (int x=0; x<5&&!connected; x++){
            connected = pingURL(websiteURL+"Ping");
       }

       //If no ping received, give up and quit.
           if (!connected){
               Toast.makeText(this, "No Server Communication", Toast.LENGTH_LONG).show();
               Toast.makeText(this, "Networking stopping", Toast.LENGTH_SHORT).show();
               stopSelf();
       }
    }

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();


     //Class used for the client Binder.  Because we know this service always
     // runs in the same process as its clients, we don't need to deal with IPC.

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
    public int onShoot() {
        int killID=0;

        return killID;
    }

    public int onStab(){
        int killID=0;

        return killID;
    }

    public long[] onSearch(){
        long[] playerInfo=null;

        return playerInfo;
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
        int httpCode=503;
        //Communicate with server

        //If a 204 is returned, player is shot. If a 206 is returned, stabbed, 200 is success, 400 is failure
        switch(httpCode){
            case 200:

                break;
            case 204:

                break;
            case 206:

                break;
            case 400:
                Toast.makeText(this, "Invalid Player Info Given!", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Server Com. Error!", Toast.LENGTH_SHORT).show();
                break;
        }



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
        //Here we need to get the score and cash from the server
    }

    public int updateDeathTimer(){
        int DeathTimer=0;
        //Get the deathtimer value from the server.
        return DeathTimer;
    }

    //Ping code for networking. Straight from StackOverflow. Ask Alex to make sure it's legit.
    private static boolean pingURL(final String address) {
        try {
            final URL url = new URL(address+"Ping");
            final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(1000 * 10); // mTimeout is in seconds
            //final long startTime = System.currentTimeMillis();
            urlConn.connect();
            //final long endTime = System.currentTimeMillis();
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //Debug statements
                //System.out.println("Time (ms) : " + (endTime - startTime));
                //System.out.println("Ping to "+address +" was success");
                return true;
            }
        } catch (final MalformedURLException e1) {
            e1.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
