package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class SendHandCommand implements Command {

    private static final long serialVersionUID = 21312300945L;
    private Card[] hand;

    public SendHandCommand(Card[] hand)
    {
        System.out.println("hand command");
        this.hand = hand;
    }

    public void execute(CommandInvoker invoker) {
        invoker.model.setHand(hand);
    }
}
