package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerMove;
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

    private List<PlayerUser> players;
    private Card[] communityCards;
    private int myID;
    private State state;
    public final Bet bet;
    public final MyPlayer myPlayer;

    public static final String TAG = "gameModel";

    public GameViewModel(Socket client, ObjectOutputStream out)
    {
        this.client = client;
        this.out = out;
        communityCards = new Card[5];
        players = new ArrayList<>();
        this.bet = new Bet();
        myPlayer = new MyPlayer();
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
        players.clear();
        players.addAll(newPlayers);
        setChanged();
        notifyObservers(players);
    }

    public List<PlayerUser> getPlayers()
    {
        return players;
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
        System.arraycopy(cards, 0, communityCards, 0, cards.length);

        setChanged();
        notifyObservers(cards);
    }

    public void setTurn(Card card)
    {
        Log.d(TAG,"setTurn");
        communityCards[3] = card;
        setChanged();
        notifyObservers(communityCards[3]);
    }

    public void setRiver(Card card)
    {
        Log.d(TAG,"setRiver");
        communityCards[4] = card;
        setChanged();
        notifyObservers(communityCards[4]);
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
        if(getState() == State.CALL || getState() == State.CHECK || move == PlayerUserMove.EXIT)
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
            updateState(State.READY);
            Log.d(TAG, "pressed button");
        }
    }

    public void lastTurn(PlayerMove move)
    {
        if(move.move == PlayerUserMove.RAISE)
        {
            bet.addToPot(move.bet);
            bet.setLastRaise(move.bet);
        }
        setChanged();
        notifyObservers(move);
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
        private int lastRaise;

        public void setBlind(int blind)
        {
            Log.d(TAG, "blind set");
            if(blind > this.blind)
            {
                this.blind = blind;
                setChanged();
                notifyObservers(blind);
            }
            addToPot(blind);
        }

        public void addToPot(int bet)
        {
            pot += bet;
        }

        public void resetPot()
        {
            pot = 0;
            blind = 0;
        }

        public int calcMinRaise()
        {
            if(lastRaise == 0)
            {
                return blind;
            }
            return pot + lastRaise;
        }

        public void setLastRaise(int raise)
        {
            lastRaise = raise;
        }

        public void resetLastRaise()
        {
            lastRaise = 0;
        }
    }

    public class MyPlayer extends Observable
    {
        public void setHand(Card[] hand)
        {
            getMyPlayer().setHand(hand);

            setChanged();
            notifyObservers(hand);
            Log.d(TAG,"setHand: " + hand[0].getCardRank().toString() + ", " + hand[1].getCardRank().toString());
        }

        public Card[] getMyHand()
        {
            Log.d(TAG,"getHand");
            return getMyPlayer().getHand();
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

        public int getMyID()
        {
            return getMyPlayer().getID();
        }
    }
}
