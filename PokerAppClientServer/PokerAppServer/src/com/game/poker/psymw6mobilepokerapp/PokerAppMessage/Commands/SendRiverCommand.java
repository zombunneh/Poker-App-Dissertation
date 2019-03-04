package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class SendRiverCommand implements Command {

    private static final long serialVersionUID = 3005834784L;
    private Card card;

    public SendRiverCommand(Card card)
    {
        this.card = card;
    }

    public void execute(CommandInvoker invoker) {

    }
}
