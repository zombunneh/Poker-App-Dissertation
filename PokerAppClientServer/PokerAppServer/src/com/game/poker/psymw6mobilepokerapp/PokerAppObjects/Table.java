package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppDatabase.QueryDBForUserDetails;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.*;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands.*;
import com.game.poker.psymw6mobilepokerapp.PokerAppServer.ClientConnection;
import com.game.poker.psymw6mobilepokerapp.PokerAppServer.ServerCallback;
import com.game.poker.psymw6mobilepokerapp.PokerAppServer.ServerRunnable;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
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
    private static final int MAX_USERS = 6;
    private int noUsersAtTable = 0;
    private boolean gameStarted = false;

    private GameRunnable game;
    private Queue queue;
    private HashMap<Integer, ObjectOutputStream> playerOutputMap;
    private HashMap<Integer, ClientConnection> playerInputMap;
    private List<PlayerUser> players;

    private QueryDBForUserDetails db;

    private boolean tableClosed = false;

    //TODO AFK MONITORING THREAD ?? ?    ? ? ?   ?  ? ? ? ?? ? ? ?? ? ? ? ?
    public Table(int tableID, Queue queue)
    {
        this.tableID = tableID;
        currentUserID = 1;
        this.queue = queue;
        playerOutputMap = new HashMap<>();
        playerInputMap = new HashMap<>();
        players = new ArrayList<>();
        db = new QueryDBForUserDetails();
        System.out.println("Table created with id: " + tableID);
    }

    public void addUserToTable(SocketUser user) throws IOException
    {
        user.getConnection().getOut().writeObject("game_joined");
        System.out.println("sent game joined");
        PlayerUser temp = new PlayerUser(user);
        playerOutputMap.put(currentUserID, user.getConnection().getOut());
        playerInputMap.put(currentUserID, user.getConnection());
        temp.setID(currentUserID);
        //TODO send command to client to set ID
        sendToUser(temp.getID(), new SetIDCommand(temp.getID()));
        players.add(temp);
        currentUserID++;
        noUsersAtTable++;
        if(gameStarted)
        {
            game.updateGamePlayerList(temp);
            game.updateTable(this);
        }
        System.out.println("user added to table: " + temp.username + " \ncurrent users at table = " + players.size());
    }

    public int getOpenSeats()
    {
        return (MAX_USERS - noUsersAtTable);
    }

    public List<PlayerUser> getPlayers()
    {
        return players;
    }

    public PlayerUserTurn sendToUser(int id, Command command)
    {
        if(playerOutputMap.containsKey(id))
        {
            try
            {
                ObjectOutputStream out = playerOutputMap.get(id);
                out.writeObject(command);
                out.flush();

                if(command.getClass() == CanCallCommand.class || command.getClass() == CanCheckCommand.class)
                {
                    return playerInputMap.get(id).getPlayerMove();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
                if(e instanceof SocketTimeoutException)
                {

                }
                else {
                    return new PlayerUserTurn(PlayerUserMove.EXIT, 0);
                }
            }
        }
        return null;
    }

    public void sendToAllUser(Command command)
    {
        for(ObjectOutputStream out : playerOutputMap.values())
        {
            try
            {
                out.reset();
                out.writeObject(command);
                out.flush();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void removeFromTable(int id)
    {
        playerInputMap.remove(id);
        playerOutputMap.remove(id);
        noUsersAtTable--;

        queue.removeUser(players.get(id-1));

        db.updateUserDetailsOnChange(game.getPlayer(id));

        players.remove(id-1);

        game.updateTable(this);
        game.removePlayer(id);
    }

    @Override
    public void run() {
        while(true)
        {
            game = new GameRunnable(this);
            Thread gameThread = new Thread(game, "game " + tableID);
            gameThread.start();
            gameStarted = true;
            System.out.println("Created new game in separate thread.");
            System.out.println("Now monitoring players in this table thread with id: " + tableID);

            while(noUsersAtTable >= 0)
            {
                if(getOpenSeats() != 0)
                {
                    queue.addOpenTable(this);
                }
                if(tableClosed)
                    break;
            }
            break;
        }
        System.out.println("Table thread with id: " + tableID + " has ended.");
    }

    @Override
    public int compareTo(Table otherTable) {
        return (this.noUsersAtTable - otherTable.noUsersAtTable);
    }

    public void closeTable()
    {
        tableClosed = true;
    }

    public boolean getEndGameFromTable()
    {
        if(game != null) {
            return game.getEndGame();
        }
        return false;
    }
}
