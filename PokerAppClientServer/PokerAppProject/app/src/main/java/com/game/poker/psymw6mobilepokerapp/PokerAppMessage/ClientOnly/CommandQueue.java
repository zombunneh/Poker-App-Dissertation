package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly;

import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands.Command;

import java.util.ArrayList;
import java.util.List;

public class CommandQueue {
    private List<Command> commands;

    public static final String TAG = "command_queue";

    public CommandQueue()
    {
        commands = new ArrayList<>();
    }

    public List<Command> getCommandList()
    {
        return commands;
    }

    public synchronized Command getNextCommand()
    {
        return commands.remove(0);
    }

    public synchronized void addCommand(Command command)
    {
        //Log.d(TAG, "command added");
        commands.add(command);
        notify();
    }

    public boolean isEmpty()
    {
        if(commands.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
