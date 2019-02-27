package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;

import java.util.*;

/**
 *  Data structure for the players in a single game
 */
public class GamePlayerList {
    private HashMap<Integer, PlayerUser> players;
    public GamePlayerList()
    {
        players = new HashMap<>();
    }

    public List<PlayerUser> getPlayers()
    {
        return new ArrayList<>(players.values());
    }

    public PlayerUser getNextPlayer(PlayerUser user)
    {
        int id = user.getID();
        int nextID = 0;
        boolean nextIDFound = false;
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

    public PlayerUser getRandomPlayer()
    {
        Random random = new Random();
        if(players.size() == 0)
        {
            return null;
        }
        return getPlayers().get(random.nextInt(players.size()));
    }

    public void addPlayer(PlayerUser user)
    {
        user.toggleActive();
        players.put(user.getID(), user);
    }

    public void removePlayer(int id)
    {
        players.remove(id);
    }

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

    public List<PlayerUser> getMinList()
    {
        List<PlayerUser> temp = new ArrayList<>();
        for(PlayerUser user : getPlayers())
        {
            user.setHand(null);
            PlayerUser tempUser = user;
            temp.add(tempUser);
        }
        return temp;
    }

    public List<PlayerUser> getPlayersLeft()
    {
        List<PlayerUser> temp = new ArrayList<>();
        for(PlayerUser player : getPlayers())
        {
            if(!player.isFolded())
            {
                temp.add(player);
            }
        }
        return temp;
    }

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
