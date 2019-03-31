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
        System.out.println("send turn notification" + id);
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
