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

    /**
     * Command Invoker object runs in a thread executing commands from the command queue
     * which is added to by the Game Listener
     *
     * @param client The client socket used for GameViewModel object
     * @param out The ObjectOutputStream used for GameViewModel object
     * @param queue The command queue the invoker will execute commands from
     * @param viewContext The calling activity context
     */
    public CommandInvoker(Socket client, ObjectOutputStream out, CommandQueue queue, Context viewContext )
    {
        this.model = new GameViewModel(client, out);
        this.viewContext = viewContext;

        this.controller = new GameViewController(model, viewContext);
        this.queue = queue;

        model.addObserver(controller);
        model.bet.addObserver(controller);
        model.myPlayer.addObserver(controller);
    }

    /**
     * Thread of execution going over the command queue executing each command in it
     */
    @Override
    public void run() {
        Command command;
        //Log.d(TAG, "invoker running");
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
                        //Log.d(TAG,"executing command");
                        command.execute(this);
                    }
                    else
                    {
                        //Log.d(TAG, "no command to execute");
                    }
                }
            }
        }
    }

    /**
     * Method to start and stop the invoker object
     *
     * @param invoke True to start the invoker, false to stop it
     */
    public void startInvoker(boolean invoke)
    {
        invoked = invoke;
    }

    /**
     * Checks if the invoker object is running or not
     *
     * @return True if CommandInvoker is running, false if not running or final loop
     */
    public boolean isInvoked() {
        return invoked;
    }

    /**
     * Getter for the GameViewModel object created in the constructor
     *
     * @return GameViewModel object containing the data required for the game view
     */
    public GameViewModel getModel() {
        return model;
    }
}
