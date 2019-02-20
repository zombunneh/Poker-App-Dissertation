package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;

import java.util.HashMap;

/**
 *  Data structure for the players in a single game
 */
public class GamePlayerList {
    private HashMap<Integer, PlayerUser> players;
    public GamePlayerList()
    {
        players = new HashMap<>();
    }

    public PlayerUser getNextPlayer()
    {
        return new PlayerUser("100", 0, "asd");
    }

    public void addPlayer(PlayerUser user)
    {
        players.put(user.getID(), user);
    }

    public void removePlayer(int id)
    {
        players.remove(id);
    }

}
