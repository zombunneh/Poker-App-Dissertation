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

    /**
     * Constructor for the model component which manages the underlying data required for the game
     *
     * @param client
     * @param out
     */
    public GameViewModel(Socket client, ObjectOutputStream out)
    {
        this.client = client;
        this.out = out;
        communityCards = new Card[5];
        players = new ArrayList<>();
        this.bet = new Bet();
        myPlayer = new MyPlayer();
    }

    /**
     * Sets the client's ID
     *
     * @param id The ID to set
     */
    public void setID(int id)
    {
        myID = id;
        Log.d(TAG,"setID");
    }

    /**
     * Getter for the client's ID
     *
     * @return The client's ID
     */
    public int getID()
    {
        return myID;
    }

    /**
     * Updates the list of players in the game
     *
     * @param newPlayers The new list of players
     */
    public void updatePlayerList(List<PlayerUser> newPlayers)
    {
        Log.d(TAG,"updateList");
        players.clear();
        players.addAll(newPlayers);
        for(PlayerUser player : newPlayers)
        {
            Log.d(TAG, "player has: " + Integer.toString(player.getCurrency()));
        }
        setChanged();
        notifyObservers(players);
    }

    /**
     * Getter for the current list of players in the game
     *
     * @return A list of PlayerUsers
     */
    public List<PlayerUser> getPlayers()
    {
        return players;
    }

    /**
     * Getter for an individual player in the game
     *
     * @param id The ID of the player
     * @return A PlayerUser object representing the player
     */
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

    /**
     * Getter for the community cards
     *
     * @return An array of the community cards
     */
    public Card[] getCommunityCards()
    {
        return communityCards;
    }

    /**
     * Setter for the flop community cards
     *
     * @param cards The 3 card array of flop cards
     */
    public void setFlop(Card[] cards)
    {
        Log.d(TAG,"setFlop");
        System.arraycopy(cards, 0, communityCards, 0, cards.length);

        setChanged();
        notifyObservers(cards);
    }

    /**
     * Setter for the turn community card
     *
     * @param card The turn card
     */
    public void setTurn(Card card)
    {
        Log.d(TAG,"setTurn");
        communityCards[3] = card;
        setChanged();
        notifyObservers(communityCards[3]);
    }

    /**
     * Setter for the river community card
     *
     * @param card The river card
     */
    public void setRiver(Card card)
    {
        Log.d(TAG,"setRiver");
        communityCards[4] = card;
        setChanged();
        notifyObservers(communityCards[4]);
    }

    /**
     * Getter for the current state
     *
     * @return A state object
     */
    public State getState()
    {
     return this.state;
    }

    /**
     * Setter for the game state
     *
     * @param state The new state
     */
    public void updateState(State state)
    {
        Log.d(TAG, "changed state " + state.toString());
        this.state = state;
        setChanged();
        notifyObservers(state);
    }

    /**
     * Called when a button is pressed and sends the button press to the server
     *
     * @param move The button pressed
     * @param bet The amount bet
     */
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

    /**
     * Sets the last turn received and updates bet if necessary
     *
     * @param move The move made in the last turn
     */
    public void lastTurn(PlayerMove move)
    {
        if(move.move == PlayerUserMove.RAISE)
        {
            bet.addToPot(move.bet);
            bet.setLastRaise(move.bet);
        }
        else if(move.move == PlayerUserMove.CALL)
        {
            bet.addToPot(move.bet);
        }
        setChanged();
        notifyObservers(move);
    }

    public enum State
    {
        READY,
        CHECK,
        CALL,
    }

    /**
     * Inner class representing the game bet states
     */
    public class Bet extends Observable
    {
        private int blind;
        private int pot;
        private int betCall;
        private int lastRaise;

        /**
         * Setter for the blind amount
         *
         * @param blind The amount of the blind
         */
        public void setBlind(int blind)
        {
            Log.d(TAG, "blind set " + blind);
            if(blind > this.blind)
            {
                this.blind = blind;
                /*setChanged();
                notifyObservers(blind);*/
            }
            addToPot(blind);
        }

        /**
         * Increments the pot amount
         *
         * @param bet The amount to increase the pot by
         */
        public void addToPot(int bet)
        {
            Log.d(TAG, "adding: " + bet + " to pot");
            pot += bet;
            setChanged();
            notifyObservers(pot);
        }

        /**
         * Resets the pot and blind
         */
        public void resetPot()
        {
            pot = 0;
            blind = 0;
        }

        /**
         * Calculates the minimum amount to bet/raise by
         *
         * @return The min raise or bet
         */
        public int calcMinRaise()
        {
            if(lastRaise == 0)
            {
                return blind;
            }
            return pot + lastRaise;
        }

        /**
         * Setter for the last raise made
         *
         * @param raise The amount raised by
         */
        public void setLastRaise(int raise)
        {
            lastRaise = raise;
        }

        /**
         * Resets the last raise amount
         */
        public void resetLastRaise()
        {
            lastRaise = 0;
        }
    }

    /**
     * Inner class representing the client's player state
     */
    public class MyPlayer extends Observable
    {
        /**
         * Setter for the player's hand
         *
         * @param hand The array of cards for the hand
         */
        public void setHand(Card[] hand)
        {
            getMyPlayer().setHand(hand);

            setChanged();
            notifyObservers(hand);
            Log.d(TAG,"setHand: " + hand[0].getCardRank().toString() + ", " + hand[1].getCardRank().toString());
        }

        /**
         * Getter for the player's hand
         *
         * @return The array of cards of the hand
         */
        public Card[] getMyHand()
        {
            Log.d(TAG,"getHand");
            return getMyPlayer().getHand();
        }

        /**
         * Getter for the player
         *
         * @return A PlayerUser object of the client's player
         */
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

        /**
         * Getter for the player's ID
         *
         * @return The ID of the client's player
         */
        public int getMyID()
        {
            return getMyPlayer().getID();
        }
    }
}
