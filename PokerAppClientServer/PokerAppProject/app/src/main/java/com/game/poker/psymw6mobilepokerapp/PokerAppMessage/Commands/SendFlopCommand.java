package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;

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
     * Uses supplied invoker to set the flop cards and also resets bets and raises for the player
     * ready for a new round of betting
     *
     * @param invoker The invoker that will execute the command
     */
    public void execute(CommandInvoker invoker) {
        invoker.getModel().setFlop(cards);
        invoker.getModel().bet.resetLastRaise();
        for(PlayerUser player : invoker.getModel().getPlayers())
        {
            player.resetBet();
            player.resetLastBet();
        }
    }
}
