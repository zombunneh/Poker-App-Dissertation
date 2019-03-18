package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class SendTurnNotificationCommand implements Command {
    private int id;

    public SendTurnNotificationCommand(int id)
    {
        this.id = id;
    }

    public void execute(CommandInvoker invoker) {
        invoker.controller.playerTurn(this.id);
    }
}
