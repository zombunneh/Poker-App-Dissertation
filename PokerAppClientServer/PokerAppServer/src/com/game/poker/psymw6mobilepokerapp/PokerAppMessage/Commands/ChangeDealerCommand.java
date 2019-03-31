package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class ChangeDealerCommand implements Command {

    private static final long serialVersionUID = 19285733483L;
    private int oldID;
    private int newID;

    /**
     * Sends the previous and current dealer to clients
     *
     * @param oldDealerID ID of the previous dealer
     * @param newDealerID ID of the new dealer
     */
    public ChangeDealerCommand(int oldDealerID, int newDealerID)
    {
        System.out.println("change dealer command");
        this.oldID = oldDealerID;
        this.newID = newDealerID;
    }

    /**
     * Implemented client side
     *
     * @param invoker Invoker to execute commands
     */
    public void execute(CommandInvoker invoker) {

    }
}
