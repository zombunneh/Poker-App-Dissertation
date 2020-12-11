package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;


import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;

import java.util.List;

public class SendPlayerListCommand implements Command {

    private static final long serialVersionUID = 17593862563L;
    private List<PlayerUser> players;

    /**
     * Sends a list of players to clients
     *
     * @param players The new list of players
     */
    public SendPlayerListCommand(List<PlayerUser> players)
    {
        this.players = players;
    }

    /**
     * Uses the supplied invoker to update the client player list
     *
     * @param invoker The invoker that will execute the command
     */
    public void execute(CommandInvoker invoker) {
        invoker.getModel().updatePlayerList(players);
    }
}
