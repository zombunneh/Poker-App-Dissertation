package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.SocketUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.User;

import java.util.List;
/*
    users added into table, handles *admin* things
    if not at max capacity, add self to queue list of tables, remove if full
    assign each user to playeruser-> give them an id
    add users to table
    remove users from table if:
        disconnected
        no response in set time
    create a gamerunnable to handle com.game logic

    maintain a list of client id's and their sockets / outputs streams
    maintain a list of player objects
 */
public class Table implements Comparable<Table>, Runnable{
    public int tableID;
    private static final int MAX_USERS = 8;
    private int userTurn = 1; // probs not needed here
    private List<User> playersAtTable;
    private int gameStage = 1; // probs not needed here
    private int noUsersAtTable = 0;
    private boolean tableOpen = true;
    private GameRunnable game;
    private Queue queue;

    public Table(int tableID)
    {
        this.tableID = tableID;
        queue = new Queue();
        System.out.println("Table created with id: " + tableID);
    }

    public void addUserToTable(SocketUser user)
    {
        noUsersAtTable++;
    }

    public int getOpenSeats()
    {
        return (MAX_USERS - noUsersAtTable);
    }

    @Override
    public void run() {
        game = new GameRunnable(this);
        Thread gameThread = new Thread(game);
        gameThread.start();
        System.out.println("Created new com.game in separate thread.");
        System.out.println("Now monitoring players in this table thread with id: " + tableID);

        while(noUsersAtTable > 0)
        {
            if(getOpenSeats() == 0)
            {
                queue.removeTable(this);
            }
            else
            {
                queue.addOpenTable(this);
            }
        }

        System.out.println("Table thread with id: " + tableID + " has ended.");
    }

    @Override
    public int compareTo(Table otherTable) {
        return (this.noUsersAtTable - otherTable.noUsersAtTable);
    }
}
