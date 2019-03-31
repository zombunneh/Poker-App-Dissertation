package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class SendRiverCommand implements Command {

    private static final long serialVersionUID = 3005834784L;
    private Card card;

    /**
     * Sends the river card to clients
     *
     * @param card The turn card
     */
    public SendRiverCommand(Card card)
    {
        this.card = card;
    }

    /**
     * Implemented client side
     *
     * @param invoker Invoker to execute commands
     */
    public void execute(CommandInvoker invoker) {

    }
}
