package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly;

import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands.Command;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameView;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameViewController;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameViewModel;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class CommandInvoker implements Runnable{
    public final GameViewModel model;
    public final GameViewController controller;
    public final GameView view;
    private CommandQueue queue;

    private boolean invoked = false;

    public static final String TAG = "command_invoker";

    public CommandInvoker(Socket client, ObjectOutputStream out, CommandQueue queue )
    {
        this.model = new GameViewModel(client, out);
        this.controller = new GameViewController();
        this.view = new GameView();
        this.queue = queue;
    }

    @Override
    public void run() {
        Command command;
        while(invoked)
        {
            synchronized (queue)
            {
                if(queue.isEmpty())
                {
                    try
                    {
                        queue.wait();
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    command = queue.getNextCommand();
                    if(command != null)
                    {
                        command.execute(this);
                        Log.d(TAG,"executing command");
                    }
                    else
                    {
                        Log.d(TAG, "no command to execute");
                    }
                }
            }
        }
    }

    public void stopInvoker()
    {
        invoked = true;
    }

}