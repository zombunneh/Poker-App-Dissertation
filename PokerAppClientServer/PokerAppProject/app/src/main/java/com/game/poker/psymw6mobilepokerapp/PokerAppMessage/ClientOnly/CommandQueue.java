package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands.Command;

import java.util.ArrayList;
import java.util.List;

public class CommandQueue {
    private List<Command> commands;

    /**
     * CommandQueue class maintains a list of commands as they are sent to the client for the invoker to execute
     */
    public CommandQueue()
    {
        commands = new ArrayList<>();
    }

    /**
     * Getter for the command list object
     *
     * @return The list of commands
     */
    public List<Command> getCommandList()
    {
        return commands;
    }

    /**
     * Synchronized method to prevent concurrency issues retrieving commands
     *
     * @return The next Command at the head of the list
     */
    public synchronized Command getNextCommand()
    {
        return commands.remove(0);
    }

    /**
     * Synchronised method to prevent concurrency issues adding commands
     *
     * @param command The command to add to the list
     */
    public synchronized void addCommand(Command command)
    {
        commands.add(command);
        notify();
    }

    /**
     * Checks if the command list is empty
     *
     * @return True if command list is empty, false if not
     */
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
