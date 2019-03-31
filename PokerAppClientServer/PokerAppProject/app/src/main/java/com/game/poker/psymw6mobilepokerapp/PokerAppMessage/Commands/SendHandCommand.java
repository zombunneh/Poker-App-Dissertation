package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class SendHandCommand implements Command {

    private static final long serialVersionUID = 21312300945L;
    private Card[] hand;

    /**
     * Sends the hand of a player to their client
     *
     * @param hand Array of 2 cards making up the hand
     */
    public SendHandCommand(Card[] hand)
    {
        System.out.println("hand command");
        this.hand = hand;
    }

    /**
     * Uses the supplied invoker to update the client's player's own hand
     *
     * @param invoker The invoker that will execute the command
     */
    public void execute(CommandInvoker invoker) {
        invoker.getModel().myPlayer.setHand(hand);
    }
}
