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

    /**
     * Constructor for a game object setting up all the game variables required
     *
     * @param table The table object to be used to send commands and remove players in the game
     */
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

    /**
     * Adds player to the list of players in the game and sends them a list of all players
     *
     * @param user The user to be added
     */
    public void updateGamePlayerList(PlayerUser user)
    {
        players.addPlayer(user);
        table.sendToUser(user.getID(), new SendPlayerListCommand(players.getMinList()));
    }

    /**
     * Updates the store table object
     *
     * @param table The table to update with
     */
    public void updateTable(Table table)
    {
        this.table = table;
    }

    /**
     * Sets gameRunning to false which will terminate the loop on the next iteration and end the GameRunnable
     */
    public void endGame()
    {
        System.out.println("endgame called");
        gameRunning = false;
    }

    /**
     * Checks to see whether the game thread has ended or not
     *
     * @return True if the game has ended, false if not
     */
    public boolean getEndGame()
    {
        return !gameRunning;
    }

    /**
     * Removes a player from the game list
     *
     * @param id The player to remove
     */
    public void removePlayer(int id)
    {
        //will create separate thread for removal, which monitors players that !ingame
        System.out.println("remove player");
        players.removePlayer(id);
    }

    /**
     *
     * @param id The id of the player to return
     * @return Returns a PlayerUser object of the player with the id requested
     */
    public PlayerUser getPlayer(int id)
    {
        return players.getPlayer(id);
    }

    /**
     * Core game loop
     */
    @Override
    public void run() {
        System.out.println("Game thread started from table with id: " + table.tableID);
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
        System.out.println("game thread " + table.tableID + " ended");
    }

    /**
     * Handles setup of the game necessary to beginning the hand
     * Resets community cards, pot and current amount to call
     * Reset game state to 0 and sets game to not be ended
     * Shuffles deck
     *
     * Changes dealer and sets up each players status and hand
     */
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
        for(PlayerUser player : players.getMinList())
        {
            System.out.println("player: " + player.getID() + " currency: " + player.getCurrency());
        }
        table.sendToAllUser(new SendPlayerListCommand(players.getMinList()));

        PlayerUser oldDealer = players.setNextDealer();
        dealer = players.getDealer();
        table.sendToAllUser(new ChangeDealerCommand(oldDealer.getID(), dealer.getID()));
        for(PlayerUser user : players.getPlayers())
        {
            System.out.println("users setup");
            if(user.isInGame())
            {
                if(!user.isActive())
                {
                    user.toggleActive();
                }
                user.unFold();
                Card[] tempHand = new Card[2];
                tempHand[0] = deck.drawCard();
                tempHand[1] = deck.drawCard();
                user.setHand(tempHand);
                user.resetBet();
                user.resetLastBet();
                table.sendToUser(user.getID(), new SendHandCommand(tempHand));
                System.out.println("setting hand for ID: " + user.getID());
                System.out.println("player with ID: " + user.getID() + " has currency: " + user.getCurrency());
            }
        }
    }

    /**
     * Sets up the blinds for the game and starts the first round of betting
     */
    public void preFlop()
    {
        PlayerUser nextPlayer = players.getNextPlayer(dealer);
        //set small blind
        nextPlayer.setCurrentBet(blind / 2);
        table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.BLIND, nextPlayer.getID(), nextPlayer.getLastBet(), nextPlayer.getCurrency()))); //todo NEED TO IMPLEMENT THIUS OMFG WOWOWWOWOWOWOW
        System.out.println("set small blind for player " + nextPlayer.username);
        raise(nextPlayer.getLastBet());

        System.out.println("pot " + pot);

        nextPlayer = players.getNextPlayer(nextPlayer);
        //set big blind
        nextPlayer.setCurrentBet(blind);
        table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.BLIND, nextPlayer.getID(), nextPlayer.getLastBet(), nextPlayer.getCurrency())));
        System.out.println("set big blind for player " + nextPlayer.username);
        raise(nextPlayer.getLastBet());

        System.out.println("pot " + pot);

        //first round of betting
        initialPlayer = players.getNextPlayer(nextPlayer);
        bettingRound(1);
    }

    /**
     * Draws the flop cards and sends them to all users and starts the second round of betting
     */
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

    /**
     * Draws the turn card and sends it to all users and starts the third round of betting
     */
    public void turn()
    {
        Card turnCard = deck.drawCard();
        communityCards.add(turnCard);
        System.out.println("turn added to table cards");

        table.sendToAllUser(new SendTurnCommand(turnCard));
        bettingRound(3);
    }

    /**
     * Draws the river card and sends it to all users and starts the fourth round of betting
     */
    public void river()
    {
        Card riverCard = deck.drawCard();
        communityCards.add(riverCard);
        System.out.println("river added to table cards");

        table.sendToAllUser(new SendRiverCommand(riverCard));
        bettingRound(4);
    }

    /**
     * End of hand uses the HandEvaluator class to determine the winner of the hand and then accordingly updates all players stats in the game
     */
    public void endHand()
    {
        System.out.println("hand ending");
        HashMap<PlayerUser, Hand> winners;
        List<PlayerUser> winnerList = new ArrayList<>();
        if(players.getPlayersLeft().size() == 0)
        {

        }
        else {
            winners = handEvaluator.handEvaluator(players.getPlayersLeft(), communityCards);
            winners = handEvaluator.getHandWinner(winners);
            winnerList.addAll(winners.keySet());
            if (winnerList.size() > 0) {
                for (int i = 0; i < winnerList.size(); i++) {
                    System.out.println("winner: " + winnerList.get(i).username + " with hand " + winners.get(winnerList.get(i)).toString() + " wins: " + (pot / winnerList.size()));
                    PlayerUser current = players.getPlayer(winnerList.get(i).getID());
                    current.giveCurrency(pot / winnerList.size());
                    current.incrementWins();
                    if (current.max_chips < current.getCurrency()) {
                        current.newMaxChips(current.getCurrency());
                    }
                    if (current.max_winnings < (pot / winnerList.size())) {
                        current.newMaxWinnings((pot / winnerList.size()));
                    }

                }
            }

            table.sendToAllUser(new SendWinCommand(winners, pot));
        }

        for(PlayerUser player : players.getActivePlayers())
        {
            player.incrementHandsPlayed();
            player.adjustWinRate();
        }

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

    /**
     * Increases pot by amount bet and increases the amount needed to call if the bet is higher than the previous amount to call
     *
     * @param bet The amount bet
     */
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

    /**
     * Notifies players of who has the current turn and then sends the player whose turn it is the appropriate notification if they can call or check
     * Waits for a response and then based on the move of the player appropriately changes game state variables
     *
     * @param round The round of betting
     */
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
                        table.sendToAllUser(new SendTurnNotificationCommand(better.getID()));
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

                        System.out.println("player: " + better.username + " " + better.getID() + " move: " + turn.move.toString());

                        switch (turn.move) {
                            case AWAY: {
                                better.toggleActive();
                                table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.AWAY, better.getID(), better.getLastBet(), better.getCurrency())));
                                System.out.println("player: " + better.username + " went afk");
                                better.incrementInactive();
                                //needs finishing
                            }
                            break;
                            case EXIT: {
                                better.fold();
                                table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.EXIT, better.getID(), better.getLastBet(), better.getCurrency())));
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
                                table.sendToAllUser(new SendPlayerMoveCommand(new PlayerMove(PlayerUserMove.CHECK, better.getID(), better.getLastBet(), better.getCurrency())));
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
