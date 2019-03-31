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

    private static final int TIMEOUT = 3000;

    public static final String SERVICE_INTENT = "service_intent";

    public static final String TAG = "queue_runnable";

    /**
     * Constructor for a separate thread to join the game queue
     *
     * @param clientSocket The client's socket
     * @param out Output stream connected to server
     * @param in Input stream from server
     * @param context Context object for context specific methods
     */
    public JoinQueue(Socket clientSocket, ObjectOutputStream out, ObjectInputStream in, Context context)
    {
        this.clientSocket = clientSocket;
        this.out = out;
        this.in = in;
        this.mContext = context;
    }

    /**
     * Tells the server to join the queue and waits for confirmation that first the queue was joined and then when a game is joined
     */
    @Override
    public void run() {
        try
        {
            Log.d(TAG, "joining queue");
            out.writeObject("join_queue");
            clientSocket.setSoTimeout(TIMEOUT);
            Object object = null;
            while(!(object instanceof String))
            {
                object = in.readObject();
            }
            String confirm = (String) object;
            if (confirm.equals("queue_joined")) {
                sendBroadcastMessage(confirm);
            } else {
                sendBroadcastMessage("unable_to_join_queue");
            }
            clientSocket.setSoTimeout(0);
            String game = (String) in.readObject();
            if (game.equals("game_joined")) {
                mContext.startActivity(new Intent(mContext, GameView.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        } catch(IOException | ClassNotFoundException e)
        {
            Log.d(TAG, e.toString());
            if(e instanceof SocketException)
            {

            }
        }

    }

    /**
     * Sends a local broadcast containing a string message
     *
     * @param message The message to send
     */
    private void sendBroadcastMessage(String message)
    {
        Log.d(TAG, "sending broadcast" + message);
        Intent intent = new Intent(SERVICE_INTENT);
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
