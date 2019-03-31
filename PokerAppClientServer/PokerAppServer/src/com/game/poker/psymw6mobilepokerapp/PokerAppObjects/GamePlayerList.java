package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  Data structure for the players in a single game
 */
public class GamePlayerList {
    private ConcurrentHashMap<Integer, PlayerUser> players;
    public GamePlayerList()
    {
        players = new ConcurrentHashMap<>();
    }

    /**
     *
     * @return A list of players stored in the object
     */
    public List<PlayerUser> getPlayers()
    {
        return new ArrayList<>(players.values());
    }

    /**
     * Finds the adjacent player to the one given stored in the hashmap
     *
     * @param user The player to find next from
     * @return The player with the next id in the list from the parameter
     */
    public PlayerUser getNextPlayer(PlayerUser user)
    {
        int id = user.getID();
        int nextID = 0;
        boolean nextIDFound = false;
        if(players.size() < 1)
        {
            return null;
        }
        else
        {
            int maxKey = Collections.max(players.keySet());
            int minKey = Collections.min(players.keySet());
            for(int i = 1; i <= maxKey; i++)
            {
                if(players.containsKey(id+i) && !nextIDFound)
                {
                    nextID = id+i;
                    nextIDFound = true;
                }
            }

            if(!nextIDFound)
            {
                nextID = minKey;
            }

            return players.get(nextID);
        }
    }

    /**
     *
     * @return A random PlayerUser from the list or null if the list is empty
     */
    public PlayerUser getRandomPlayer()
    {
        Random random = new Random();
        if(players.size() == 0)
        {
            return null;
        }
        return getPlayers().get(random.nextInt(players.size()));
    }

    /**
     * Adds a player to the hashmap
     *
     * @param user The player to add
     */
    public void addPlayer(PlayerUser user)
    {
        user.toggleActive();
        players.put(user.getID(), user);
    }

    /**
     * Removes a player from the hashmap
     *
     * @param id The id of the player to remove
     */
    public void removePlayer(int id)
    {
        players.remove(id);
    }

    /**
     *
     * @param id The id of the player to retrieve
     * @return The PlayerUser with the corresponding id
     */
    public PlayerUser getPlayer(int id)
    {
        return players.get(id);
    }

    /**
     *
     * @return The PlayerUser that is currently the dealer, or if none is set then a random player is chosen and set as dealer
     */
    public PlayerUser getDealer()
    {
        for(PlayerUser user: getPlayers())
        {
            if(user.getDealer())
            {
                return user;
            }
        }
        PlayerUser randomDealer = getRandomPlayer();
        if(randomDealer == null)
        {
            return null;
        }
        randomDealer.setDealer();
        return randomDealer;
    }

    /**
     * Toggles the current dealer to be not dealer and gets the next player and sets them as the dealer
     *
     * @return The old dealer
     */
    public PlayerUser setNextDealer()
    {
        PlayerUser prevDealer = getDealer();
        if(prevDealer == null)
        {
            return null;
        }
        prevDealer.setDealer();
        getNextPlayer(prevDealer).setDealer();
        return prevDealer;
    }

    /**
     * Constructs a new list from the current hashmap by first setting each player's hand to null to prevent potential exploits when sent
     * To users who don't need to know that information
     *
     * @return A list of PlayerUsers
     */
    public List<PlayerUser> getMinList()
    {
        List<PlayerUser> temp = new ArrayList<>();
        for(PlayerUser user : getPlayers())
        {
            PlayerUser tempUser = user;
            tempUser.setHand(null);
            temp.add(tempUser);
        }
        return temp;
    }

    /**
     *
     * @return A list of players left in the game who haven't folded or gone inactive
     */
    public List<PlayerUser> getPlayersLeft()
    {
        List<PlayerUser> temp = new ArrayList<>();
        for(PlayerUser player : getPlayers())
        {
            if(!player.isFolded() && player.isActive())
            {
                temp.add(player);
            }
        }
        return temp;
    }

    /**
     *
     * @return A list of players who are not inactive
     */
    public List<PlayerUser> getActivePlayers()
    {
        List<PlayerUser> temp = new ArrayList<>();
        for(PlayerUser player : getPlayers())
        {
            if(player.isActive())
            {
                temp.add(player);
            }
        }
        return temp;
    }

    /**
     *
     * @return A list of players who are still active and have chips to bet with
     */
    public List<PlayerUser> movesLeft()
    {
        List<PlayerUser> temp = new ArrayList<>();
        for(PlayerUser player : getPlayersLeft())
        {
            if(!player.isFolded() && player.isActive() && player.getCurrency() > 0)
            {
                temp.add(player);
            }
        }
        return temp;
    }
}
