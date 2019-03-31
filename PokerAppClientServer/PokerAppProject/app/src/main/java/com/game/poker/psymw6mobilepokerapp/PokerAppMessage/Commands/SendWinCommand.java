package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.Hand;
import java.util.HashMap;

public class SendWinCommand implements Command {

    private static final long serialVersionUID = 321655784L;
    private HashMap<PlayerUser, Hand> players;
    private int winnings;

    /**
     * Sends a hasmap of winners and the amount won
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
     * Uses supplied invoker to set the winner list and reset the pot ready for the next hand
     *
     * @param invoker The invoker that will execute the command
     */
    public void execute(CommandInvoker invoker) {
        invoker.controller.winnerList(players, winnings);
        invoker.getModel().bet.resetPot();
    }
}
