package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameViewModel {
    private Socket client;
    private ObjectOutputStream out;

    private PlayerUser myPlayer;
    private List<PlayerUser> players;
    private Card[] communityCards;
    private int myID;
    private State state;

    public GameViewModel(Socket client, ObjectOutputStream out)
    {
        this.client = client;
        this.out = out;
        communityCards = new Card[5];
        players = new ArrayList<>();
    }

    public void setID(int id)
    {
        myID = id;
    }

    public int getID()
    {
        return myID;
    }

    public void updatePlayerList(List<PlayerUser> newPlayers)
    {
        players.addAll(newPlayers);
    }

    public void setHand(Card[] hand)
    {
        for(PlayerUser player : players)
        {
            if(player.getID() == myID)
            {
                player.setHand(hand);
            }
        }
    }

    public Card[] getHand(int id)
    {
        for(PlayerUser player : players)
        {
            if(player.getID() == id)
            {
                return player.getHand();
            }
        }
        return null;
    }

    public PlayerUser getMyPlayer()
    {
        PlayerUser temp;
        for(PlayerUser player : players)
        {
            if(player.getID() == myID)
            {
                temp = player;
                return temp;
            }
        }
        return null;
    }

    public PlayerUser getPlayer(int id)
    {
        PlayerUser temp;
        for(PlayerUser player : players)
        {
            if(player.getID() == id)
            {
                temp = player;
                return temp;
            }
        }
        return null;
    }

    public Card[] getCommunityCards()
    {
        return communityCards;
    }

    public void setFlop(Card[] cards)
    {
        for(int i = 0; i < cards.length; i++)
        {
            communityCards[i] = cards[i];
        }
    }

    public void setTurn(Card card)
    {
        communityCards[3] = card;
    }

    public void setRiver(Card card)
    {
        communityCards[4] = card;
    }

    public void updateState(State state)
    {
        this.state = state;
    }

    public void updateController()
    {

    }

    public enum State
    {
        READY,
        CHECK,
        CALL
    }
}
