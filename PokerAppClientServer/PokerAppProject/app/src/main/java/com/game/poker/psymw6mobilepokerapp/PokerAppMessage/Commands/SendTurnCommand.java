package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;

public class SendTurnCommand implements Command {

    private static final long serialVersionUID = 3016634784L;
    private Card card;

    public SendTurnCommand(Card card)
    {
        this.card = card;
    }

    public void execute(CommandInvoker invoker) {
        invoker.getModel().setTurn(card);
        invoker.getModel().bet.resetLastRaise();
        for(PlayerUser player : invoker.getModel().getPlayers())
        {
            player.resetBet();
            player.resetLastBet();
        }
    }
}
