package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

import java.io.Serializable;

public class PlayerUserTurn implements Serializable {

    public PlayerUserMove move;
    public int bet;

    private static final long serialVersionUID = 9486023485L;

    public PlayerUserTurn(PlayerUserMove move, int bet)
    {
        this.move = move;
        this.bet = bet;
    }

    public int getBet()
    {
        return bet;
    }

}
