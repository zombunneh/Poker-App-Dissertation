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
    private boolean gameRunning = true;

    private int gameDelay;

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
        gameDelay = 5;
    }

    public void updateGamePlayerList(PlayerUser user)
    {
        user.toggleInGame();
        players.addPlayer(user);
        table.sendToUser(user.getID(), new SendPlayerListCommand(players.getMinList()));
    }

    public void updateTable(Table table)
    {
        this.table = table;
    }

    public void endGame()
    {
        gameRunning = false;
    }

    public void removePlayer(int id)
    {
        //will create separate thread for removal, which monitors players that !ingame
        System.out.println("remove player");
        players.removePlayer(id);
    }

//TODO CONDITION TO END HAND EARLY IF ALL ALL IN
    @Override
    public void run() {
        System.out.println("Game thread started from table with id: " + table.tableID);
        while(players.getPlayers().size() < 2)
        {

        }
        dealer = players.getDealer(); // sets initial dealer
        while(gameRunning)
        {
            for(int i = gameDelay; i > 0; i--)
            {
                try
                {
                    System.out.println("next hand in: " + i);
                    Thread.sleep(1000);
                }
                catch(InterruptedException e)
                {

                }
            }
            if(players.getPlayers().size() < 1)
            {
                endGame();
                table.closeTable();
            }
            else
            {
                preHand();
                preFlop();
                flop();
                turn();
                river();
                endHand();
            }
        }
    }

    public void preHand()
    {
        System.out.println("prehand");
        communityCards.clear();
        deck.shuffleDeck();
        pot = 0;
        betCall = 0;
        currentGameState = 0;
        gameEnd = false;
        Card[] dummyCards = new Card[3];
        table.sendToAllUser(new SendFlopCommand(dummyCards));
        table.sendToAllUser(new SendTurnCommand(null));
        table.sendToAllUser(new SendRiverCommand(null));
        //remember need to set IDs for users and send Player List
        //need to tell client to start activity
        table.sendToAllUser(new SendPlayerListCommand(players.getMinList()));
        PlayerUser oldDealer = players.setNextDealer();
        dealer = players.getDealer();
        table.sendToAllUser(new ChangeDealerCommand(oldDealer.getID(), dealer.getID()));
        for(PlayerUser user : players.getPlayers())
        {
            System.out.println("users setup");
            if(user.isActive())
            {
                if(!user.isInGame())
                {
                    user.toggleInGame();
                }
                user.unFold();
                Card[] tempHand = new Card[2];
                tempHand[0] = deck.drawCard();
                tempHand[1] = deck.drawCard();
                user.setHand(tempHand);
                user.resetBet();
                table.sendToUser(user.getID(), new SendHandCommand(tempHand));
                System.out.println("setting hand for ID: " + user.getID());
                System.out.println("player with ID: " + user.getID() + " has currency: " + user.getCurrency());
            }
        }
    }

    public void preFlop()
    {
        PlayerUser nextPlayer = players.getNextPlayer(dealer);
        //set small blind
        nextPlayer.setCurrentBet(blind / 2);
        table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.BLIND, nextPlayer.getID(), nextPlayer.getCurrentBet(), nextPlayer.getCurrency()))); //todo NEED TO IMPLEMENT THIUS OMFG WOWOWWOWOWOWOW
        System.out.println("set small blind for player " + nextPlayer.username);
        raise(nextPlayer.getLastBet());

        System.out.println("pot " + pot);

        nextPlayer = players.getNextPlayer(nextPlayer);
        //set big blind
        nextPlayer.setCurrentBet(blind);
        table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.BLIND, nextPlayer.getID(), nextPlayer.getCurrentBet(), nextPlayer.getCurrency())));
        System.out.println("set big blind for player " + nextPlayer.username);
        raise(nextPlayer.getLastBet());

        System.out.println("pot " + pot);

        //first round of betting
        initialPlayer = players.getNextPlayer(nextPlayer);
        bettingRound(1);
    }

    public void flop()
    {
        Card[] flopCards = new Card[3];
        for(int i = 0; i < 3; i++)
        {
            flopCards[i] = deck.drawCard();
            communityCards.add(flopCards[i]);
        }
        System.out.println("flop added to table cards");

        table.sendToAllUser(new SendFlopCommand(flopCards));
        bettingRound(2);
    }

    public void turn()
    {
        Card turnCard = deck.drawCard();
        communityCards.add(turnCard);
        System.out.println("turn added to table cards");

        table.sendToAllUser(new SendTurnCommand(turnCard));
        bettingRound(3);
    }

    public void river()
    {
        Card riverCard = deck.drawCard();
        communityCards.add(riverCard);
        System.out.println("river added to table cards");

        table.sendToAllUser(new SendRiverCommand(riverCard));
        bettingRound(4);
    }

    public void endHand()
    {
        System.out.println("hand ending");
        HashMap<PlayerUser, Hand> winners;
        List<PlayerUser> winnerList = new ArrayList<>();
        winners = handEvaluator.handEvaluator(players.getPlayersLeft(), communityCards);
        winners = handEvaluator.getHandWinner(winners);
        winnerList.addAll(winners.keySet());

        for(int i = 0; i < winnerList.size(); i++)
        {
            System.out.println("winner: " + winnerList.get(i).username + " with hand " + winners.get(winnerList.get(i)).toString() + " wins: " + (pot / winnerList.size()));
            players.getPlayer(winnerList.get(i).getID()).giveCurrency(pot / winnerList.size());
        }

        table.sendToAllUser(new SendWinCommand(winnerList, pot));
        System.out.println("hand ended");
    }

    public void endHandEarly()
    {
        for(Card card : communityCards)
        {
            if(card == null)
            {
                card = deck.drawCard();
            }
        }

        endHand();
    }

    public void raise(int bet)
    {
        if(bet>=betCall)
        {
            betCall = bet;
            pot += bet;
        }
        else
        {
            pot += bet;
        }
    }

    public void bettingRound(int round)
    {
        PlayerUser better = initialPlayer;
        PlayerUserTurn turn;
        while(currentGameState<round)
        {
            System.out.println("betting round: " + round);
            do
            {
                if(!gameEnd)
                {
                    if (!better.isFolded() && better.getCurrency() > 0 && better.isActive()) // remember to
                    {
                        System.out.println("getting user turn");
                        if (better.getCurrentBet() == betCall) {
                            //send command for check option
                            turn = table.sendToUser(better.getID(), new CanCheckCommand());
                        } else {
                            //send command for call option
                            turn = table.sendToUser(better.getID(), new CanCallCommand());
                        }
                        if (turn == null) {
                            turn = new PlayerUserTurn(PlayerUserMove.AWAY, 0);
                        }

                        System.out.println("player: " + better.username + " move: " + turn.move.toString());

                        switch (turn.move) {
                            case AWAY: {
                                better.toggleActive();
                                table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.AWAY, better.getID(), better.getCurrentBet(), better.getCurrency())));
                                System.out.println("player: " + better.username + " went afk");
                                better.incrementInactive();
                                //needs finishing
                            }
                            break;
                            case EXIT: {
                                better.fold();
                                table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.EXIT, better.getID(), better.getCurrentBet(), better.getCurrency())));
                                table.removeFromTable(better.getID());
                                System.out.println("player: " + better.username + " left the game");
                            }
                            break;
                            case CALL: {
                                better.setCurrentBet(betCall - better.getCurrentBet());
                                raise(better.getLastBet());
                                table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.CALL, better.getID(), better.getLastBet(), better.getCurrency())));
                                System.out.println("player: " + better.username + " calls " + better.getLastBet());
                            }
                            break;
                            case CHECK: {
                                table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.CHECK, better.getID(), better.getCurrentBet(), better.getCurrency())));
                                System.out.println("player: " + better.username + " checks");
                            }
                            break;
                            case FOLD: {
                                better.fold();
                                table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.FOLD, better.getID(), better.getLastBet(), better.getCurrency())));
                                System.out.println("player: " + better.username + " folds");
                            }
                            break;
                            case RAISE: {
                                raise(turn.getBet());
                                better.setCurrentBet(turn.getBet());
                                initialPlayer = better;
                                table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.RAISE, better.getID(), better.getLastBet(), better.getCurrency())));
                                System.out.println("player: " + better.username + " raises " + better.getLastBet());
                            }
                            break;
                        }
                   /* if(players.getPlayersLeft().size()<2 && players.movesLeft().size()<2) // 1 extra condition
                    {
                        //need to check for flop/river/turn and set if not set removed whilst debugging only 1 player >:3
                        endHand();
                    }*/
                    } else if (!better.isFolded() && !better.isActive() && better.getInactivity() < 5) {
                        System.out.println("increment");
                        better.incrementInactive();
                    }
                    if (better.getInactivity() == 5) {
                        table.removeFromTable(better.getID());
                    }
                    better = players.getNextPlayer(better);
                    if (better == null) {
                        gameEnd = true;
                    }
                }
            }
            while(better != initialPlayer && !gameEnd);
            currentGameState++;
            betCall = 0;
            for(PlayerUser player : players.getPlayers())
            {
                player.resetBet();
            }
        }
    }
}
