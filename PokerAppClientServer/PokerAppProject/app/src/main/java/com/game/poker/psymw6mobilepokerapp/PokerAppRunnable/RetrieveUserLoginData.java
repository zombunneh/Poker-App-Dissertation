package com.game.poker.psymw6mobilepokerapp.PokerAppRunnable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.GameUser;
import com.game.poker.psymw6mobilepokerapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/*
    Created by psymw6
    30/01/2019
    RetrieveUserLoginData.java
*/

/*
change this to bind to connection service to grab the data
make sure to immediately persist data in shared prefs
 */
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

    public RetrieveUserLoginData(GoogleSignInAccount account, ObjectOutputStream out, ObjectInputStream in, Context mContext)
    {
        this.account = account;
        this.out = out;
        this.in = in;
        this.context = mContext;

        accountType = 0;
        isUsernameSet = false;

        Log.d(TAG, "ruld created");
        LocalBroadcastManager.getInstance(context).registerReceiver(myReceiver, new IntentFilter(CREATE_USER_INTENT));
        saveData = context.getSharedPreferences(
                context.getString(R.string.dataPreferences),
                Context.MODE_PRIVATE);
    }

    public RetrieveUserLoginData(String instanceID, ObjectOutputStream out, ObjectInputStream in, Context mContext)
    {
        this.instanceID = instanceID;
        this.out = out;
        this.in = in;
        this.context = mContext;

        accountType = 1;
        isUsernameSet = false;

        Log.d(TAG, "ruld created");
        LocalBroadcastManager.getInstance(context).registerReceiver(myReceiver, new IntentFilter(CREATE_USER_INTENT));
        saveData = context.getSharedPreferences(
                context.getString(R.string.dataPreferences),
                Context.MODE_PRIVATE);
    }

    @Override
    public void run() {
        Log.d(TAG, "ruld thread started");
        try {
            Log.d(TAG, "starting communication");
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

            Log.d(TAG, "sent details to server");
            boolean accountExists = in.readBoolean();
            Log.d(TAG, "account exists returned:" + accountExists);
            if(accountExists)
            {
                GameUser user = (GameUser) in.readObject();
                populateSharedPrefs(user);
                sendBroadcastMessage("loginDetailsUpdated");
                //populate shared prefs
                //broadcast completion
            }
            else
            {
                Log.d(TAG, "account doesnt exist, executing new account protocol");
                sendBroadcastMessage("accountNotFound");
                //need to tell client to ask for username
                //use local broadcast?
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
                Log.d(TAG, "sending username to server " + username);

                out.writeObject(username);
                Log.d(TAG, "waiting for new user details");
                GameUser user = (GameUser) in.readObject();
                //TODO LOGIN DATA CALCULATION FOR DAILY BONUS
                populateSharedPrefs(user);
                sendBroadcastMessage("loginDetailsUpdated");
            }
        }
        catch(IOException | ClassNotFoundException e)
        {
            Log.d(TAG, "Error: " + e.toString());
        }
    }

    private void populateSharedPrefs(GameUser user)
    {
        SharedPreferences.Editor editSaveData = saveData.edit();
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
        editSaveData.commit();
        //TODO remember to analyse last login date
    }


    private void sendBroadcastMessage(String message)
    {
        Log.d(TAG, "sending broadcast");
        Intent intent = new Intent(BROADCAST_INTENT);
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            username = intent.getStringExtra("message");
            Log.d(TAG, "message received: " + username);
            isUsernameSet = true;
        }
    };
}
/*
poker game stats:
hands played
hands won
win rate
max winnings
max chips
 */