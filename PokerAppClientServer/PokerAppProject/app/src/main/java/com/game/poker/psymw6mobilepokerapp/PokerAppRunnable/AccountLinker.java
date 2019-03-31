package com.game.poker.psymw6mobilepokerapp.PokerAppRunnable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AccountLinker implements Runnable {

    private Socket clientSocket;
    private Context mContext;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private SharedPreferences prefs;
    private GoogleSignInAccount account;
    private String guest_id;

    public static final String LINKER_INTENT = "linker_intent";
    public static final String TAG = "accLink";

    public AccountLinker(Socket clientSocket, ObjectOutputStream out, ObjectInputStream in, Context context, GoogleSignInAccount account, String id)
    {
        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;
        this.mContext = context;
        this.account = account;
        this.guest_id = id;
        prefs = mContext.getSharedPreferences(
                mContext.getString(R.string.loginPreferences),
                Context.MODE_PRIVATE);
    }

    @Override
    public void run() {
        try
        {
            String idtoken = account.getIdToken();

            clientSocket.setSoTimeout(3000);
            out.writeObject("link_account");
            out.writeObject(idtoken);
            out.writeObject(guest_id);
            out.flush();

            String response = (String) in.readObject();

            if(response.equals("account_linked"))
            {
                sendBroadcastMessage("account_linked");
            }
            else
            {
                sendBroadcastMessage("account_link_fail");
            }
        } catch(IOException | ClassNotFoundException e)
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
        Intent intent = new Intent(LINKER_INTENT);
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
