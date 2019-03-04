package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class SendFlopCommand implements Command{

    private static final long serialVersionUID = 19285234093L;
    private Card[] cards;

    public SendFlopCommand(Card[] cards)
    {
        this.cards = cards;
    }

    public void execute(CommandInvoker invoker) {
        invoker.model.setFlop(cards);
    }
}
