package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;

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
     * Uses supplied invoker to set the river card and also resets bets and raises for the player
     * ready for a new round of betting
     *
     * @param invoker The invoker that will execute the command
     */
    public void execute(CommandInvoker invoker) {
        invoker.getModel().setRiver(card);
        invoker.getModel().bet.resetLastRaise();
        for(PlayerUser player : invoker.getModel().getPlayers())
        {
            player.resetBet();
            player.resetLastBet();
        }
    }
}
