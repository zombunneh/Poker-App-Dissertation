package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

import java.io.Serializable;

public class PlayerMove implements Serializable {
    public PlayerUserMove move;
    public int id;
    public int bet;
    public int currency;

    public static final long serialVersionUID = 1524785049L;

    /**
     * Class used to notify players of other player's move details
     *
     * @param move The player's move
     * @param id The id of the player who made the move
     * @param bet The amount bet by the player
     * @param currency The amount of currency held by the player
     */
    public PlayerMove(PlayerUserMove move, int id, int bet, int currency)
    {
        this.move = move;
        this.id = id;
        this.bet = bet;
        this.currency = currency;
    }

}
