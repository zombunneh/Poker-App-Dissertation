package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.*;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands.*;

import java.util.ArrayList;
import java.util.HashMap;
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

    private PlayerUser dealer;
    private PlayerUser initialPlayer;
    private int blind;
    private int pot;
    private int betCall;
    private int currentGameState;
    private boolean gameEnd;

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

        blind = 50;
        pot = 0;
        betCall = 0;
        currentGameState = 0;
        gameEnd = false;
    }

    public void updateGamePlayerList(PlayerUser user)
    {
        players.addPlayer(user);
    }

    public void updateTable(Table table)
    {
        this.table = table;
    }
//TODO CONDITION TO END HAND EARLY IF ALL ALL IN
    @Override
    public void run() {
        System.out.println("Game thread started from table with id: " + table.tableID);
        dealer = players.getDealer(); // sets initial dealer
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
        pot = 0;
        currentGameState = 0;
        //remember need to set IDs for users and send Player List
        PlayerUser oldDealer = players.setNextDealer();
        dealer = players.getDealer();
        table.sendToAllUser(new ChangeDealerCommand(oldDealer.getID(), dealer.getID()));
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
        PlayerUser nextPlayer = players.getNextPlayer(dealer);
        //set small blind
        nextPlayer.setCurrentBet(blind / 2);
        pot += blind / 2;
        table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.BLIND, nextPlayer.getID(), nextPlayer.getCurrentBet(), nextPlayer.getCurrency()))); //todo NEED TO IMPLEMENT THIUS OMFG WOWOWWOWOWOWOW
        System.out.println("set small blind for player " + nextPlayer.username);
        raise(nextPlayer.getCurrentBet());

        nextPlayer = players.getNextPlayer(nextPlayer);
        //set big blind
        nextPlayer.setCurrentBet(blind);
        pot += blind;
        table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.BLIND, nextPlayer.getID(), nextPlayer.getCurrentBet(), nextPlayer.getCurrency())));
        System.out.println("set big blind for player " + nextPlayer.username);
        raise(nextPlayer.getCurrentBet());

        //first round of betting
        initialPlayer = players.getNextPlayer(nextPlayer);
        bettingRound(1);
    }

    public void flop()
    {
        communityCards.add(deck.drawCard());
        communityCards.add(deck.drawCard());
        communityCards.add(deck.drawCard());
        System.out.println("flop added to table cards");

        table.sendToAllUser(new SendFlopCommand());

        bettingRound(2);
    }

    public void turn()
    {
        communityCards.add(deck.drawCard());
        System.out.println("turn added to table cards");

        table.sendToAllUser(new SendTurnCommand());

        bettingRound(3);
    }

    public void river()
    {
        communityCards.add(deck.drawCard());
        System.out.println("river added to table cards");

        table.sendToAllUser(new SendRiverCommand());

        bettingRound(4);
    }

    public void endHand()
    {
        HashMap<PlayerUser, Hand> winners;
        List<PlayerUser> winnerList = new ArrayList<>();
        winners = handEvaluator.handEvaluator(players.getPlayersLeft(), communityCards);
        winners = handEvaluator.getHandWinner(winners);
        winnerList.addAll(winners.keySet());

        for(int i = 0; i < winnerList.size(); i++)
        {
            System.out.println("winner: " + winnerList.get(i) + " with hand " + winners.get(winnerList.get(i)).toString());
        }

        table.sendToAllUser(new SendWinCommand(winnerList, pot));
    }

    public void raise(int bet)
    {
        if(bet>betCall)
        {
            betCall = bet;
        }
    }

    public void bettingRound(int round)
    {
        PlayerUser better = initialPlayer;
        PlayerUserTurn turn;
        while(currentGameState<round)
        {
            do
            {
                if(!better.isFolded() && better.getCurrency()>0 && better.isActive()) // remember to
                {
                    if(better.getCurrentBet()==betCall)
                    {
                        //send command for check option
                        turn = table.sendToUser(better.getID(), new CanCheckCommand());
                    }
                    else
                    {
                        //send command for call option
                        turn = table.sendToUser(better.getID(), new CanCallCommand());
                    }
                    if(turn == null)
                    {
                        turn = new PlayerUserTurn(PlayerUserMove.AWAY, 1);
                    }

                    System.out.println("player: " + better.username + "move: " + turn.move.toString());

                    switch(turn.move)
                    {
                        case AWAY:
                        {
                            better.toggleActive();
                            table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.AWAY, better.getID(), better.getCurrentBet(), better.getCurrency())));
                            System.out.println("player: " + better.username + " went afk");
                            //needs finishing
                        }
                            break;
                        case EXIT:
                        {
                            better.fold();
                            table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.EXIT, better.getID(), better.getCurrentBet(), better.getCurrency())));
                            table.removePlayer(better);
                            players.removePlayer(better.getID());
                            System.out.println("player: " + better.username + " left the game");
                        }
                            break;
                        case CALL:
                        {
                            better.setCurrentBet(betCall - better.getCurrentBet());
                            table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.CALL, better.getID(), better.getCurrentBet(), better.getCurrency())));
                            System.out.println("player: " + better.username + " calls");
;                        }
                            break;
                        case CHECK:
                        {
                            table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.CHECK, better.getID(), better.getCurrentBet(), better.getCurrency())));
                            System.out.println("player: " + better.username + " checks");
                        }
                            break;
                        case FOLD:
                        {
                            better.fold();
                            table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.FOLD, better.getID(), better.getCurrentBet(), better.getCurrency())));
                            System.out.println("player: " + better.username + " folds");
                        }
                            break;
                        case RAISE:
                        {
                            raise(turn.getBet());
                            better.setCurrentBet(turn.getBet());
                            initialPlayer = better;
                            table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.RAISE, better.getID(), better.getCurrentBet(), better.getCurrency())));
                            System.out.println("player: " + better.username + " raises");
                        }
                            break;
                    }
                    if(players.getPlayersLeft().size()<2 && players.movesLeft().size()<2) // 1 extra condition
                    {
                        endHand();
                    }
                }
                better = players.getNextPlayer(better);
            }
            while(better != initialPlayer && !gameEnd);
            if(!gameEnd)
            {
                currentGameState++;
            }
        }
    }
}
