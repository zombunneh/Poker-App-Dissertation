package com.game.poker.psymw6mobilepokerapp.PokerAppRunnable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.GameUser;
import com.game.poker.psymw6mobilepokerapp.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ProfileRetriever implements Runnable{

    private Socket clientSocket;
    private Context mContext;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GameUser user;
    private SharedPreferences prefs;
    private RetrieveUserLoginData retrieveMethod;

    public static final String RETRIEVE_INTENT = "retrieve_intent";

    public static final String TAG = "prof_retrieve";

    /**
     * Constructor for a separate thread to retrieve an updated user profile
     *
     * @param clientSocket The client's socket
     * @param out Output stream connected to server
     * @param in Input stream from server
     * @param context Context object for context specific methods
     */
    public ProfileRetriever(Socket clientSocket, ObjectOutputStream out, ObjectInputStream in, Context context)
    {
        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;
        this.mContext = context;
        prefs = mContext.getSharedPreferences(
                mContext.getString(R.string.dataPreferences),
                Context.MODE_PRIVATE);
        retrieveMethod = new RetrieveUserLoginData();
    }

    @Override
    public void run() {
        try
        {
            clientSocket.setSoTimeout(3000);
            out.writeObject("retrieve_profile");
            Object object = null;
            while(!(object instanceof GameUser))
            {
                object = in.readObject();
            }
            user = (GameUser) object;

            retrieveMethod.populateSharedPrefs(user, prefs, mContext);
            sendBroadcastMessage("profile_retrieved");
        }
        catch(IOException | ClassNotFoundException e)
        {
            Log.d(TAG, e.toString());
        }
    }

    /**
     * Sends a local broadcast containing a string message
     *
     * @param message The message to send
     */
    private void sendBroadcastMessage(String message)
    {
        Intent intent = new Intent(RETRIEVE_INTENT);
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
