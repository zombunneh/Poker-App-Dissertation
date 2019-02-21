package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;

import java.util.ArrayList;
import java.util.List;

/*
    com.game logic/flow here:
    how to communicate to client
    how to change com.game state
 */
public class GameRunnable implements Runnable{
    private EvaluateHand handEvaluator;
    private Deck deck;
    private Table table;
    private GamePlayerList players;
    private List<Card> communityCards;

    public GameRunnable(Table table)
    {
        this.table = table;
        deck = new Deck();
        handEvaluator = new EvaluateHand();
        players = new GamePlayerList();
        communityCards = new ArrayList<>();
        deck.shuffleDeck();
        for(PlayerUser user : table.getPlayers())
        {
            players.addPlayer(user);
        }
    }

    public void updateGamePlayerList(PlayerUser user)
    {
        players.addPlayer(user);
    }

    public void updateTable(Table table)
    {
        this.table = table;
    }

    @Override
    public void run() {
        System.out.println("Game thread started from table with id: " + table.tableID);
        while(true)
        {
            preHand();
        }
    }

    public void preHand()
    {
        players.getNextDealer();

    }

    public void preFlop()
    {

    }

    public void flop()
    {

    }

    public void turn()
    {

    }

    public void river()
    {

    }

    public void endHand()
    {

    }
}
