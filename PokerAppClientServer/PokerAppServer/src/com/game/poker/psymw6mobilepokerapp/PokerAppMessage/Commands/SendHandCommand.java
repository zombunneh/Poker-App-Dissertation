package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class SendHandCommand implements Command {
    private Card[] hand;

    public SendHandCommand(Card[] hand)
    {
        this.hand = hand;
    }

    public void execute(CommandInvoker invoker) {

    }
}
