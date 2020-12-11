package com.game.poker.psymw6mobilepokerapp.PokerAppService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppRunnable.AccountLinker;
import com.game.poker.psymw6mobilepokerapp.PokerAppRunnable.JoinQueue;
import com.game.poker.psymw6mobilepokerapp.PokerAppRunnable.ProfileRetriever;
import com.game.poker.psymw6mobilepokerapp.PokerAppRunnable.RetrieveUserLoginData;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

public class ServerConnectionService extends Service {

    private final IBinder binder = new ServerBinder();
    private boolean serviceBound = false;

    private Socket clientSocket;
    private static final int PORT = 4567;
    public static final String HOST = "192.168.0.22";
    private RetrieveUserLoginData ruld;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public static final String TAG = "g53ids-service";
    public static final String SERVICE_INTENT = "service_intent";
    public static final String BROADCAST_INTENT = "login_intent";

    public ServerConnectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        serviceBound = true;
        return binder;
    }

    /**
     *
     */
    public class ServerBinder extends Binder
    {
        public ServerConnectionService getService()
        {
            return ServerConnectionService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Registers local broadcast receivers for communication
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        connectToServer();
        //attempt to establish connection to server
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(SERVICE_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(BROADCAST_INTENT));
        return Service.START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        serviceBound = false;
        return false;
    }

    /**
     * Attempts to connect to the server using static connection variables
     */
    public void connectToServer()
    {
        Thread serverConnectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    clientSocket = new Socket(HOST, PORT);
                    out = new ObjectOutputStream(clientSocket.getOutputStream());
                    in = new ObjectInputStream(clientSocket.getInputStream());
                }
                catch(IOException e)
                {
                    Log.d(TAG, "Error connecting: " + e.toString());
                }
            }
        });
        serverConnectionThread.start();
    }

    /**
     * Checks if the server is connected
     *
     * @return True if socket is connected to server, false if not
     */
    public boolean isServerConnected()
    {
        if(clientSocket != null)
        {
            if(clientSocket.isClosed())
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Starts a thread to retrieve user details from the server
     *
     * @param account The google account to retrieve details for
     */
    public void retrieveUserDetailsOnLogin(GoogleSignInAccount account)
    {
        if(isServerConnected())
        {
            ruld = new RetrieveUserLoginData(account, out, in, this);
            Thread t = new Thread(ruld);
            t.start();
        }
        else
        {
            connectToServer();
            while(!isServerConnected() || out == null || in == null)
            {

            }
            ruld = new RetrieveUserLoginData(account, out, in, this);
            Thread t = new Thread(ruld);
            t.start();
        }
    }

    /**
     * Starts a thread to retrieve user details from the server
     *
     * @param instanceID The guest account to retrieve details for
     */
    public void retrieveUserDetailsOnLogin(String instanceID)
    {
        if(isServerConnected())
        {
            ruld = new RetrieveUserLoginData(instanceID, out, in, this);
            Thread t = new Thread(ruld);
            t.start();
        }
        else
        {
            connectToServer();
            while(!isServerConnected() || out == null || in == null)
            {

            }
            ruld = new RetrieveUserLoginData(instanceID, out, in, this);
            Thread t = new Thread(ruld);
            t.start();
        }
    }

    /**
     * Starts a thread to join the game queue on the server
     */
    public void joinQueue()
    {
        if(isServerConnected())
        {
            JoinQueue queue = new JoinQueue(clientSocket, out, in, this);
            Thread t = new Thread(queue);
            t.start();
        }
        else
        {
            connectToServer();
            while(!isServerConnected() || out == null || in == null)
            {

            }
            JoinQueue queue = new JoinQueue(clientSocket, out, in, this);
            Thread t = new Thread(queue);
            t.start();
        }
    }

    /**
     * Starts a thread to retrieve an updated user profile from the server
     */
    public void retrieveProfile()
    {
        if(isServerConnected())
        {
            ProfileRetriever retriever = new ProfileRetriever(getClientSocket(), getOut(), getIn(), this);
            Thread t = new Thread(retriever);
            t.start();
        }
        else
        {
            connectToServer();
            while(!isServerConnected() || out == null || in == null)
            {

            }
            ProfileRetriever retriever = new ProfileRetriever(getClientSocket(), getOut(), getIn(), this);
            Thread t = new Thread(retriever);
            t.start();
        }
    }

    /**
     * Starts a thread to link a user's google account to their guest account
     */
    public void linkGoogleAccount(GoogleSignInAccount account, String guest_id)
    {
        if(isServerConnected())
        {
            AccountLinker linker = new AccountLinker(getClientSocket(), getOut(), getIn(), this, account, guest_id);
            Thread t = new Thread(linker);
            t.start();
        }
        else
        {
            connectToServer();
            while(!isServerConnected() || out == null || in == null)
            {

            }
            AccountLinker linker = new AccountLinker(getClientSocket(), getOut(), getIn(), this, account, guest_id);
            Thread t = new Thread(linker);
            t.start();
        }
    }

    /**
     * Getter for the client's socket
     *
     * @return The client's socket
     */
    public Socket getClientSocket()
    {
        return clientSocket;
    }

    /**
     * Getter for the client's socket's ObjectInputStream
     *
     * @return The socket ObjectInputStream
     */
    public ObjectInputStream getIn() {
        return in;
    }

    /**
     * Getter for the client's socket's ObjectOutputStream
     *
     * @return The socket ObjectOutputStream
     */
    public ObjectOutputStream getOut() {
        return out;
    }

    /**
     * Closes the client socket
     */
    public void closeSocket()
    {
        try
        {
            clientSocket.close();
            clientSocket = null;
            out = null;
            in = null;
        }
        catch(IOException e)
        {

        }
    }

    /**
     * Receives broadcasts from other parts of app
     */
    private final BroadcastReceiver myReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            switch(message)
            {
                case "join_queue":
                    joinQueue();
                    break;
                case "loginDetailsUpdated":
                    break;
                case "retrieve_profile":
                    retrieveProfile();
                    break;
                default:
                    break;
            }
        }
    };
}
