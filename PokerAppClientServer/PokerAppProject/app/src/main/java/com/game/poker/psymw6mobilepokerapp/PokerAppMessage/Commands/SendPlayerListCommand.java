package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;

import java.util.List;

public class SendPlayerListCommand implements Command {

    private static final long serialVersionUID = 17593862563L;
    private List<PlayerUser> players;

    public SendPlayerListCommand(List<PlayerUser> players)
    {
        System.out.println("player list command");
        this.players = players;
    }

    public void execute(CommandInvoker invoker) {
        invoker.model.updatePlayerList(players);
    }
}
