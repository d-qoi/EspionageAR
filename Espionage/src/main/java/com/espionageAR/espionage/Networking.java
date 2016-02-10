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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.String;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class Networking extends IntentService {

    ///////////////////////////////////////////////////////////////
    //Part 1: Service and networking setup.
    ///////////////////////////////////////////////////////////////

    //Set an int to control the server heartbeat. It may be useful to change this depending on
    // if the game is active/background, potentially even allow players to change it, because a
    // faster heartbeat is not difficult or taxing for us, but it does drain device battery
    private int tickCycle=1000;

    //This pair keeps track of how many coms in a row fail. If it goes over max, considers itself
    //logged out. Not implemented yet.
    private int droppedComs=0;
    private int maxDropped=5;

    //Networking initialization info. It could be considered bad form to leave the connection open
    //while the service is running, but theoretically the heartbeat should keep going constantly so
    //leaving the connection open makes sense.
    HttpClient httpClient = new DefaultHttpClient();
    private boolean initialized=false;
    private int uID;
    private String alias;
    private String saltyPass;

    //Website string. Apparently all I need to do for SSL is HTTPS because HttpClient is god?
    //I really need to talk to someone more experienced about this nuance, I think a lot of complex
    //stuff is hiding underneath all this.
    private String websiteURL="https://webserver.EspionageAR.com/API/";

    //Networking coms. This may also be good to put in a ini file.
    HttpPost createUserPost = new HttpPost(websiteURL+"CreateUser");
    HttpPost locationPost = new HttpPost(websiteURL+"Location");
    HttpPut heartbeatPut = new HttpPut(websiteURL+"Heartbeat");
    HttpPut stabPut = new HttpPut(websiteURL+"Stab");
    HttpPut shootPut = new HttpPut(websiteURL+"Shoot");
    HttpPut searchPut = new HttpPut(websiteURL+"Search");
    HttpGet createUserGet = new HttpGet (websiteURL+"CreateUser");
    HttpGet gameStateGet = new HttpGet(websiteURL+"GetGameState");
    HttpGet respawnTimerGet = new HttpGet(websiteURL+"RespawnTimer");
    HttpDelete logoutDelete = new HttpDelete(websiteURL+"Logout");

    //Related heartbeat info
    private String location;
    private int shitHappened=-1;

    //Game state info
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

    ///////////////////////////////////////////////////////////////
    //Part 2: Main action functions. Here there be HTTPS Requests.
    ///////////////////////////////////////////////////////////////

    public int onShoot() {
        int killID=0;

        //JSON Location and direction and send it to server.

        return killID;
    }

    public int onStab(){
        int killID=0;

        //JSON Location and send it to server.

        return killID;
    }

    public long[] onSearch(){
        long[] playerInfo=null;

        //JSON Location and send it to server.

        return playerInfo;
    }


    ///////////////////////////////////////////////////////////////
    //Part 3: Misc Functions. Dunno
    ///////////////////////////////////////////////////////////////

    //First attempts to log in, returns http code.
    //Second one returns if the login has happened.
    //Third one might do a heartbeat at some point
    //Fourth one returns the current game state
    //Fifth one is a function to check the death timer, which currently does not exist

    public int onLogIn(String Username,String Password,boolean newAccount){
        //Initialize the code to a service unavailable error.
        int httpCode=503;

        //JSON username/password, send to server, extract UID and response code.
        try {
            //Decide if salting password
            String SaltedPassword = new String();
            if(newAccount)
            {
                SaltedPassword = Password;
            }
            else {
                //Get the salt
                String salt = "filler";
                HttpResponse response = httpClient.execute(createUserGet);

                //Parse Response.
                httpCode = response.getStatusLine().getStatusCode();
                if(httpCode==200)
                {
                    HttpEntity entity = response.getEntity();
                    String jsonTemp = EntityUtils.toString(entity);
                    JSONObject saltResponse = new JSONObject(jsonTemp);

                    //Now that we've completed the transaction, set relevant data values
                    salt = saltResponse.getString("Salt");
                }
                //Handle Problems
                else
                    return httpCode;
                httpCode=503;

                //Salt password
                SaltedPassword = DigestUtils.sha256Hex(Password + salt);
            }

            //JSON things
            JSONObject loginInfo = new JSONObject();
            loginInfo.put("Username", Username);
            loginInfo.put("Password", SaltedPassword);
            loginInfo.put("newAccount", newAccount);

            StringEntity jsonLogin=new StringEntity(loginInfo.toString(),"UTF-8");

            //Communicate
            createUserPost.setEntity(jsonLogin);
            HttpResponse response = httpClient.execute(createUserPost);

            //Parse Response.
            httpCode = response.getStatusLine().getStatusCode();
            if(httpCode==200||httpCode==201)
            {
                HttpEntity entity = response.getEntity();
                String jsonTemp = EntityUtils.toString(entity);
                JSONObject uIDResponse = new JSONObject(jsonTemp);

                //Now that we've completed the transaction, set relevant data values
                uID = uIDResponse.getInt("UID");
                saltyPass=Password;
                alias=Username;
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "Server Comms Error!", Toast.LENGTH_SHORT).show();
        }
        finally {
        httpClient.getConnectionManager().shutdown();
        }

        //Add code here to set up the location information. Add a timeout.

        initialized=(httpCode==200||httpCode==201);
        return httpCode;
    }

    public boolean isLoggedIn(){
        return initialized;
    }

    private void heartbeat(){
        int httpCode=503;

        //JSON Location and Push to Server

        //If the server responds with info, process it.

        //If a 204 is returned, player is shot. If a 206 is returned, stabbed, 200 is success, 400 is failure
        switch(httpCode){
            case 200:
                //Nothing to do here
                break;
            case 204:
                //Do shooting things here
                break;
            case 206:
                //Do stabbing things here
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

    public int[] readGameState(){
        int[] gameState=new int [2];

        //Get game state from server.

        //Whether or not the server responds, display current info:


        return gameState;
    }

    public int updateDeathTimer(){
        //Set up the error value
        int DeathTimer=-1;

        //Get the death timer value from the server here.

        return DeathTimer;
    }


    ///////////////////////////////////////////////////////////////
    //Part 4: Random support functions, likely from StackOverflow.
    ///////////////////////////////////////////////////////////////

    //Ping code for networking.
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
