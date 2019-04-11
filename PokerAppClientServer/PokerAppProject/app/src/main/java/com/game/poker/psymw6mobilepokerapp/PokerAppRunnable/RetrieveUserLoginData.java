package com.game.poker.psymw6mobilepokerapp.PokerAppRunnable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.GameUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments.Zoomed_Cards;
import com.game.poker.psymw6mobilepokerapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RetrieveUserLoginData implements Runnable {
    private GoogleSignInAccount account;
    public static final String TAG = "g53ids-ruld";
    private String instanceID;
    private String idToken;
    private int accountType;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Context context;
    public static final String BROADCAST_INTENT = "login_intent";
    public static final String CREATE_USER_INTENT = "create_user";
    private String username;
    private boolean isUsernameSet;
    private SharedPreferences saveData;

    /**
     * Constructor for retrieving login information with a google account
     *
     * @param account The google account
     * @param out Output stream connected to server
     * @param in Input stream from server
     * @param mContext Context object for context specific methods
     */
    public RetrieveUserLoginData(GoogleSignInAccount account, ObjectOutputStream out, ObjectInputStream in, Context mContext)
    {
        this.account = account;
        this.out = out;
        this.in = in;
        this.context = mContext;

        accountType = 0;
        isUsernameSet = false;

        LocalBroadcastManager.getInstance(context).registerReceiver(myReceiver, new IntentFilter(CREATE_USER_INTENT));
        saveData = context.getSharedPreferences(
                context.getString(R.string.dataPreferences),
                Context.MODE_PRIVATE);
    }

    /**
     * Constructor for retrieving login information with a guest account
     *
     * @param instanceID The guest account ID
     * @param out Output stream connected to server
     * @param in Input stream from server
     * @param mContext Context object for context specific methods
     */
    public RetrieveUserLoginData(String instanceID, ObjectOutputStream out, ObjectInputStream in, Context mContext)
    {
        this.instanceID = instanceID;
        this.out = out;
        this.in = in;
        this.context = mContext;

        accountType = 1;
        isUsernameSet = false;

        LocalBroadcastManager.getInstance(context).registerReceiver(myReceiver, new IntentFilter(CREATE_USER_INTENT));
        saveData = context.getSharedPreferences(
                context.getString(R.string.dataPreferences),
                Context.MODE_PRIVATE);
    }

    /**
     * Default constructor
     */
    public RetrieveUserLoginData()
    {

    }

    /**
     * Thread executes account retrieval protocol with server
     */
    @Override
    public void run() {
        try {
            if(account != null)
            {
                idToken = account.getIdToken();
            }

            out.writeObject("get_account");
            out.writeInt(accountType);
            if(accountType == 0)
            {
                out.writeObject(idToken);
            }
            else
            {
                out.writeObject(instanceID);
            }

            boolean accountExists = in.readBoolean();
            if(accountExists)
            {
                GameUser user = (GameUser) in.readObject();
                //populate shared prefs
                //broadcast completion
                populateSharedPrefs(user, saveData, context);
                sendBroadcastMessage("loginDetailsUpdated");
            }
            else
            {
                sendBroadcastMessage("accountNotFound");
                while(!isUsernameSet)
                {
                    try
                    {
                        Thread.sleep(500);
                    }
                    catch(InterruptedException e)
                    {
                        Log.d(TAG, e.toString());
                    }
                }

                out.writeObject(username);
                GameUser user = (GameUser) in.readObject();
                populateSharedPrefs(user, saveData, context);
                sendBroadcastMessage("loginDetailsUpdated");
            }
        }
        catch(IOException | ClassNotFoundException e)
        {
            Log.d(TAG, "Error: " + e.toString());
        }
    }

    /**
     * Edits the shared preferences file for user details with the GameUser object received from the server
     *
     * @param user The user containing details
     */
    public void populateSharedPrefs(GameUser user, SharedPreferences prefs, Context context)
    {
        SharedPreferences.Editor editSaveData = prefs.edit();
        editSaveData.putString(context.getString(R.string.username), user.username);
        editSaveData.putInt(context.getString(R.string.currency), user.currency);
        editSaveData.putInt(context.getString(R.string.login_streak), user.loginStreak);
        editSaveData.putBoolean(context.getString(R.string.login_streak_changed), false);
        editSaveData.putInt(context.getString(R.string.hands_played), user.hands_played);
        editSaveData.putInt(context.getString(R.string.hands_won), user.hands_won);
        editSaveData.putInt(context.getString(R.string.win_rate), user.win_rate);
        editSaveData.putInt(context.getString(R.string.max_winnings), user.max_winnings);
        editSaveData.putInt(context.getString(R.string.max_chips), user.max_chips);
        editSaveData.putString(context.getString(R.string.last_login), user.lastLogin);
        editSaveData.apply();
    }

    /**
     * Sends a local broadcast containing a string message
     *
     * @param message The message to send
     */
    private void sendBroadcastMessage(String message)
    {
        Intent intent = new Intent(BROADCAST_INTENT);
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * Receiver to retrieve the username chosen by the user in the case of creating a new account
     */
    private final BroadcastReceiver myReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            username = intent.getStringExtra("message");
            isUsernameSet = true;
        }
    };
}