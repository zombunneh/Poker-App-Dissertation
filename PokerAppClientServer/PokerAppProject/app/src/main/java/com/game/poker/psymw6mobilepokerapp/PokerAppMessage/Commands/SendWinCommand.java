package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;

import java.util.List;

public class SendWinCommand implements Command {

    private static final long serialVersionUID = 321655784L;
    private List<PlayerUser> players;
    private int winnings;

    public SendWinCommand(List<PlayerUser> players, int pot)
    {
        this.players = players;
        this.winnings = pot;
    }

    public void execute(CommandInvoker invoker) {
        invoker.controller.winnerList(players, winnings);
        invoker.getModel().bet.resetPot();
    }
}
