package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

import java.io.Serializable;

public class PlayerMove implements Serializable {
    public PlayerUserMove move;
    public int id;
    public int bet;
    public int currency;

    public static final long serialVersionUID = 1524785049L;

    public PlayerMove(PlayerUserMove move, int id, int bet, int currency)
    {
        this.move = move;
        this.id = id;
        this.bet = bet;
        this.currency = currency;
    }

}
