package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands.ChangeDealerCommand;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands.SendHandCommand;
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
        players.getDealer(); // sets initial dealer
        while(true)
        {
            preHand();
            preFlop();
            flop();
            turn();
            river();
            endHand();
        }
    }

    public void preHand()
    {
        communityCards.clear();
        deck.shuffleDeck();
        //remember need to set IDs for users and send Player List
        PlayerUser oldDealer = players.setNextDealer();
        PlayerUser newDealer = players.getDealer();
        table.sendToAllUser(new ChangeDealerCommand(oldDealer.getID(), newDealer.getID()));
        for(PlayerUser user : players.getPlayers())
        {
            if(user.isActive())
            {
                user.unFold();
                Card[] tempHand = new Card[2];
                tempHand[0] = deck.drawCard();
                tempHand[1] = deck.drawCard();
                user.setHand(tempHand);
                table.sendToUser(user.getID(), new SendHandCommand(tempHand));
                System.out.println("setting hand for ID: " + user.getID());
            }
        }
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
