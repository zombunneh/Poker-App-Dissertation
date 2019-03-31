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
        System.out.println("set id command");
        this.id = id;
    }

    /**
     * Uses the supplied invoker to set the ID in
     *
     * @param invoker The invoker that will execute the command
     */
    public void execute(CommandInvoker invoker) {
        //implement to set players id
        invoker.getModel().setID(id);
    }
}
