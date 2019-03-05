package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUserMove;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUserTurn;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class GameViewModel extends Observable {
    private Socket client;
    private ObjectOutputStream out;

    private PlayerUser myPlayer;
    private List<PlayerUser> players;
    private Card[] communityCards;
    private int myID;
    private State state;
    public final Bet bet;

    public static final String TAG = "gameModel";

    public GameViewModel(Socket client, ObjectOutputStream out)
    {
        this.client = client;
        this.out = out;
        communityCards = new Card[5];
        players = new ArrayList<>();
        this.bet = new Bet();
    }

    public void setID(int id)
    {
        myID = id;
        Log.d(TAG,"setID");
    }

    public int getID()
    {
        return myID;
    }

    public void updatePlayerList(List<PlayerUser> newPlayers)
    {
        Log.d(TAG,"updateList");
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
        Log.d(TAG,"setHand");
    }

    public Card[] getHand(int id)
    {
        Log.d(TAG,"getHand");
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
        Log.d(TAG,"getPlayer");
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
        Log.d(TAG,"setFlop");
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

    public State getState()
    {
     return this.state;
    }

    public void updateState(State state)
    {
        Log.d(TAG, "changed state " + state.toString());
        this.state = state;
        setChanged();
        notifyObservers(state);
    }

    public void pressedButton(final PlayerUserMove move, final int bet)
    {
        if(getState() == State.CALL || getState() == State.CHECK)
        {
            Log.d(TAG, "pressed button");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        out.writeObject(new PlayerUserTurn(move, bet));
                    }
                    catch(IOException e )
                    {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            Log.d(TAG, "pressed button");
        }
    }

    public void updateController()
    {

    }

    public enum State
    {
        READY,
        CHECK,
        CALL,
        PLAY
    }

    public class Bet extends Observable
    {
        private int blind;
        private int pot;
        private int betCall;

        public void setBlind(int blind)
        {
            this.blind = blind;
        }
    }
}
