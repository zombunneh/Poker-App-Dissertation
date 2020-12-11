package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class SetIDCommand implements Command {

    private static final long serialVersionUID = 302348654L;
    private int id;

    /**
     * Sends an ID to clients
     *
     * @param id The ID to be sent to client
     */
    public SetIDCommand(int id)
    {
        this.id = id;
    }

    /**
     * Implemented client side
     *
     * @param invoker Invoker to execute commands
     */
    public void execute(CommandInvoker invoker) {

    }
}
