package com.game.poker.psymw6mobilepokerapp.PokerAppRunnable;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class JoinQueue implements Runnable {
    private Socket clientSocket;
    private Context mContext;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public static final String SERVICE_INTENT = "service_intent";

    public static final String TAG = "queue_runnable";

    public JoinQueue(Socket clientSocket, ObjectOutputStream out, ObjectInputStream in, Context context)
    {
        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;
        this.mContext = context;
    }

    @Override
    public void run() {
        try
        {
            out.writeObject("join_queue");
            clientSocket.setSoTimeout(3000);
            String confirm = (String) in.readObject();
            if(confirm.equals("queue_joined"))
            {
                sendBroadcastMessage(confirm);
            }
            else
            {
                sendBroadcastMessage("unable_to_join_queue");
            }

        } catch(IOException | ClassNotFoundException e)
        {
            Log.d(TAG, e.toString());
        }
    }

    private void sendBroadcastMessage(String message)
    {
        Log.d(TAG, "sending broadcast");
        Intent intent = new Intent(SERVICE_INTENT);
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
