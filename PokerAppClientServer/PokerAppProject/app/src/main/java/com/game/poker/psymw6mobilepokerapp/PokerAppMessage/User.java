package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

import java.io.Serializable;

public class User implements Serializable {
    public String user_id;
    public int currency;
    public String username;
    private static final long serialVersionUID = 548204893046L;

    /**
     * Base user constructor which each other type of user is built from
     *
     * @param user_id The id of the user
     * @param currency The amount of currency the user has
     * @param username The username of the user
     */
    public User(String user_id, int currency, String username)
    {
        this.user_id = user_id;
        this.currency = currency;
        this.username = username;
    }

    /**
     * Sets a user's currency to a given amount
     * (Mostly legacy testing feature)
     *
     * @param currency The amount to change currency to
     */
    public void setCurrency(int currency)
    {
        this.currency = currency;
    }
}
