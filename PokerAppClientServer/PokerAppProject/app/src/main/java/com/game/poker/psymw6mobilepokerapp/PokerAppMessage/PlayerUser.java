package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

public class PlayerUser extends User {
    public Card[] hand;
    private boolean isDealer;
    private boolean isFolded;
    private static final long serialVersionUID = 1452706986345L;

    public PlayerUser(String user_id, int currency, String username)
    {
        super(user_id, currency, username);
        hand = new Card[2];
        isFolded = false;
    }
}
