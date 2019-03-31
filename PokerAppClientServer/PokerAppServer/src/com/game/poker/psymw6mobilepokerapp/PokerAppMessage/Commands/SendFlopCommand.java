package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class SendFlopCommand implements Command{

    private static final long serialVersionUID = 19285234093L;
    private Card[] cards;

    /**
     * Sends the flop cards to clients
     *
     * @param cards The flop cards
     */
    public SendFlopCommand(Card[] cards)
    {
        this.cards = cards;
    }

    /**
     * Implemented client side
     *
     * @param invoker Invoker to execute commands
     */
    public void execute(CommandInvoker invoker) {

    }
}
