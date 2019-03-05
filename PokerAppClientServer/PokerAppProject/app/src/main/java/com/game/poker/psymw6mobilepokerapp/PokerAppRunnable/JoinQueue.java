package com.game.poker.psymw6mobilepokerapp.PokerAppRunnable;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandQueue;
import com.game.poker.psymw6mobilepokerapp.PokerAppService.ServerConnectionService;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

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
            clientSocket.setSoTimeout(0);
            String game = (String) in.readObject();
            if(game.equals("game_joined"))
            {
                mContext.startActivity(new Intent(mContext, GameView.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }

        } catch(IOException | ClassNotFoundException e)
        {
            Log.d(TAG, e.toString());
            if(e instanceof SocketException)
            {
                //((ServerConnectionService) mContext).connectToServer();
            }
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
