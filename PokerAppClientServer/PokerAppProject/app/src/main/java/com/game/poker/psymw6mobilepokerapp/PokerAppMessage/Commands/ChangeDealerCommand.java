package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class ChangeDealerCommand implements Command {
    private int oldID;
    private int newID;

    public ChangeDealerCommand(int oldDealerID, int newDealerID)
    {
        System.out.println("change dealer command");
        this.oldID = oldDealerID;
        this.newID = newDealerID;
    }

    public void execute(CommandInvoker invoker) {

    }
}
