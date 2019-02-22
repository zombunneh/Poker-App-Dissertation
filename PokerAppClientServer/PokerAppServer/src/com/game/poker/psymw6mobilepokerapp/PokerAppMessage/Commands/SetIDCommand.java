package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class SetIDCommand implements Command {
    private int id;

    public SetIDCommand(int id)
    {
        this.id = id;
    }

    public void execute(CommandInvoker invoker) {
        //implement to set players id
    }
}
