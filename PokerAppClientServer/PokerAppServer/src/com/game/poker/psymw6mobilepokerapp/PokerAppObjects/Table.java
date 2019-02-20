package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.SocketUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.User;
import com.game.poker.psymw6mobilepokerapp.PokerAppServer.ClientConnection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
    private int currentUserID;
    private static final int MAX_USERS = 8;
    private int noUsersAtTable = 0;
    private boolean gameStarted = false;
    private GameRunnable game;
    private Queue queue;
    private HashMap<Integer, ObjectOutputStream> playerOutputMap;
    private HashMap<Integer, ClientConnection> playerInputMap;
    private List<PlayerUser> players;

    public Table(int tableID)
    {
        this.tableID = tableID;
        currentUserID = 0;
        queue = new Queue();
        playerOutputMap = new HashMap<>();
        playerInputMap = new HashMap<>();
        players = new ArrayList<>();
        System.out.println("Table created with id: " + tableID);
    }

    public void addUserToTable(SocketUser user) throws IOException
    {
        PlayerUser temp = new PlayerUser(user.user_id, user.currency, user.username);
        playerOutputMap.put(currentUserID, new ObjectOutputStream(user.client.getOutputStream()));
        playerInputMap.put(currentUserID, new ClientConnection(user.client));
        temp.setID(currentUserID);
        players.add(temp);
        currentUserID++;
        noUsersAtTable++;
        if(gameStarted)
        {
            game.updateGamePlayerList(temp);
            game.updateTable(this);
        }
    }

    public int getOpenSeats()
    {
        return (MAX_USERS - noUsersAtTable);
    }

    public List<PlayerUser> getPlayers()
    {
        return players;
    }

    public void removePlayer(PlayerUser user)
    {
        int id = user.getID();
        playerOutputMap.remove(id);
        playerInputMap.remove(id);
        players.remove(user);
    }


    public void sendToUser(int id)
    {

    }


    public void getUserTurn(int id)
    {

    }

    @Override
    public void run() {
        while(true)
        {
            System.out.println("runnable started");
            game = new GameRunnable(this);
            Thread gameThread = new Thread(game);
            gameThread.start();
            gameStarted = true;
            System.out.println("Created new game in separate thread.");
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
            break;
        }
        System.out.println("Table thread with id: " + tableID + " has ended.");
    }

    @Override
    public int compareTo(Table otherTable) {
        return (this.noUsersAtTable - otherTable.noUsersAtTable);
    }
}
