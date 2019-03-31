package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

import java.io.Serializable;

public class PlayerUserTurn implements Serializable {

    public PlayerUserMove move;
    public int bet;

    private static final long serialVersionUID = 9486023485L;

    /**
     * A serializable class to be used as part of a client response containing their turn
     *
     * @param move The player's move
     * @param bet An optional bet amount
     */
    public PlayerUserTurn(PlayerUserMove move, int bet)
    {
        this.move = move;
        this.bet = bet;
    }

    /**
     *
     * @return The amount bet
     */
    public int getBet()
    {
        return bet;
    }

}
