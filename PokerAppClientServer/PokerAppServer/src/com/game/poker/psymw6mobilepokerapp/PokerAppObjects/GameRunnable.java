package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

//to handle com.game logic and progression
/*
    com.game logic/flow here:
    how to communicate to client
    how to change com.game state
 */
public class GameRunnable implements Runnable{
    private EvaluateHand handEvaluator;
    private Table table;

    public GameRunnable(Table table)
    {
        this.table = table;
    }

    @Override
    public void run() {
        System.out.println("Game thread started from table with id: " + table.tableID);
    }
}
