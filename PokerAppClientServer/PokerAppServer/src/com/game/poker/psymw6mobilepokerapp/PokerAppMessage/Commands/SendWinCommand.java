package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.Hand;

import java.util.*;

public class SendWinCommand implements Command {

    private static final long serialVersionUID = 321655784L;
    private HashMap<PlayerUser, Hand> players;
    private int winnings;

    /**
     * Sends a list of winners and the amount won
     *
     * @param players The winners of the hand
     * @param pot The amount won
     */
    public SendWinCommand(HashMap<PlayerUser, Hand> players, int pot)
    {
        this.players = players;
        this.winnings = pot;
    }

    /**
     * Implemented client side
     *
     * @param invoker Invoker to execute commands
     */
    public void execute(CommandInvoker invoker) {

    }
}
