package com.game.poker.psymw6mobilepokerapp.PokerAppService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppRunnable.RetrieveUserLoginData;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnectionService extends Service {

    private final IBinder binder = new ServerBinder();
    private boolean serviceBound = false;

    private Socket clientSocket;
    private static final int PORT = 4567;
    public static final String HOST = "192.168.0.22";
    private RetrieveUserLoginData ruld;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private int accountType;

    public static final String TAG = "g53ids-service";
    public static final String SERVICE_INTENT = "service_intent";

    public ServerConnectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "ServerConnectionService binded to.");
        serviceBound = true;
        return binder;
    }

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        connectToServer();
        //attempt to establish connection to server
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(SERVICE_INTENT));
        return Service.START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        serviceBound = false;
        Log.d(TAG, "ServerConnectionService unbinded.");
        return false;
    }

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
                    Log.d(TAG,"socket connected xx.");
                }
                catch(IOException e)
                {
                    Log.d(TAG, "Error connecting: " + e.toString());
                }
            }
        });
        serverConnectionThread.start();
    }

    public boolean isServerConnected()
    {
        if(clientSocket == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

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
            ruld = new RetrieveUserLoginData(account, out, in, this);
            Thread t = new Thread(ruld);
            t.start();
        }
    }

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
            ruld = new RetrieveUserLoginData(instanceID, out, in, this);
            Thread t = new Thread(ruld);
            t.start();
        }
    }

    public void joinQueue()
    {
        try
        {
            out.writeObject("join_queue");
        } catch(IOException e)
        {
            Log.d(TAG, e.toString());
        }
    }

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
                default:
                    break;
            }
        }
    };
}
