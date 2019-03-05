package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly;

import android.content.Context;
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
    public final Context viewContext;
    private CommandQueue queue;

    private boolean invoked;

    public static final String TAG = "command_invoker";

    public CommandInvoker(Socket client, ObjectOutputStream out, CommandQueue queue, Context viewContext )
    {
        this.model = new GameViewModel(client, out);
        this.viewContext = viewContext;

        this.controller = new GameViewController(model, viewContext);
        this.queue = queue;

        model.addObserver(controller);
        model.bet.addObserver(controller);
        model.myPlayer.addObserver(controller);
        Log.d(TAG, "invoker started");
    }

    @Override
    public void run() {
        Command command;
        Log.d(TAG, "invoker running");
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
                        Log.d(TAG,"executing command");
                        command.execute(this);
                    }
                    else
                    {
                        Log.d(TAG, "no command to execute");
                    }
                }
            }
        }
    }

    public void startInvoker(boolean invoke)
    {
        invoked = invoke;
    }

    public boolean isInvoked() {
        return invoked;
    }

    public GameViewModel getModel() {
        return model;
    }
}
