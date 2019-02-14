package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

import java.io.Serializable;

//only contains the information required to join a table and play a game
public class User implements Serializable {
    public String user_id;
    public int currency;
    public String username;
    public int seatNum;
    private static final long serialVersionUID = 548204893046L;

    public User(String user_id, int currency, String username)
    {
        this.user_id = user_id;
        this.currency = currency;
        this.username = username;
    }
}

