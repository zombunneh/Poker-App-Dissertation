package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class SendTurnNotificationCommand implements Command {
    private int id;

    /**
     * Sends a notification to clients of which ID's turn it is
     *
     * @param id The ID of the player whose turn it is
     */
    public SendTurnNotificationCommand(int id)
    {
        this.id = id;
    }

    /**
     * Uses supplied invoker to set the players turn to the supplied ID
     *
     * @param invoker The invoker that will execute the command
     */
    public void execute(CommandInvoker invoker) {
        invoker.controller.playerTurn(this.id);
    }
}
