package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class SendTurnCommand implements Command {
    private Card card;

    public SendTurnCommand(Card card)
    {
        this.card = card;
    }

    public void execute(CommandInvoker invoker) {
        invoker.model.setTurn(card);
    }
}