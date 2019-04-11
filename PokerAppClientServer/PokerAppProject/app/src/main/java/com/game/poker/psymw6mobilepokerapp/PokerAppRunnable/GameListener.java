package com.game.poker.psymw6mobilepokerapp.PokerAppRunnable;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandQueue;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands.Command;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands.SendPlayerListCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class GameListener implements Runnable {

    private Socket client;
    private ObjectInputStream in;
    private CommandQueue queue;
    private boolean running;
    private Context context;

    private static final int TIMEOUT = 1000;
    public static final String LISTEN_INTENT = "listen_intent";

    public static final String TAG = "GameListener";

    /**
     * Constructor for listener object used by the game to receive command objects and add them to the queue
     *
     * @param client The client's socket
     * @param in Input stream from server
     * @param queue The queue to add commands to
     */
    public GameListener(Socket client, ObjectInputStream in, CommandQueue queue, Context mContext)
    {
        this.client = client;
        this.in = in;
        this.queue = queue;
        this.context = mContext;
    }

    /**
     * Whilst running will loop read command objects and add them to the queue
     */
    @Override
    public void run() {
        Command command;
        try {
            client.setSoTimeout(TIMEOUT);
        }
        catch(SocketException e)
        {

        }
        while(running)
        {
            try
            {
                command = (Command) in.readObject();
            }
            catch(ClassNotFoundException e)
            {
                continue;
            }
            catch(SocketTimeoutException e)
            {
                command = null;
            }
            catch(IOException e)
            {
                e.printStackTrace();
                Log.d(TAG, "connection closed");
                break;
            }
            Log.d(TAG, "command received");
            if(command == null)
            {
                Log.d(TAG, "command is null");
                continue;
            }
            queue.addCommand(command);
            synchronized (queue)
            {
                queue.notify();
            }
        }
        sendBroadcastMessage("listen_end");
    }

    /**
     * Sets the running status of the thread
     *
     * @param running True to start listener, false to end
     */
    public void setRunning(boolean running)
    {
        this.running = running;
    }

    /**
     * Getter for the running status of the thread
     *
     * @return true if the listener is active, false if not
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Sends a local broadcast containing a string message
     *
     * @param message The message to send
     */
    private void sendBroadcastMessage(String message)
    {
        Intent intent = new Intent(LISTEN_INTENT);
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
