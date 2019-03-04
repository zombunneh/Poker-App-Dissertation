package com.game.poker.psymw6mobilepokerapp.PokerAppRunnable;

import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandQueue;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands.Command;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameListener implements Runnable {

    private Socket client;
    private ObjectInputStream in;
    private CommandQueue queue;
    private boolean running;

    public static final String TAG = "GameListener";

    public GameListener(Socket client, ObjectInputStream in, CommandQueue queue)
    {
        this.client = client;
        this.in = in;
        this.queue = queue;
    }

    @Override
    public void run() {
        Command command;
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
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }
}
