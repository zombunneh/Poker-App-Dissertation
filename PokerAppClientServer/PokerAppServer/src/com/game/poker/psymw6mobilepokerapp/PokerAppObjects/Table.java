package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppDatabase.QueryDBForUserDetails;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.*;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands.*;
import com.game.poker.psymw6mobilepokerapp.PokerAppServer.ClientConnection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

    private GameRunnable game;
    private Queue queue;
    private HashMap<Integer, ObjectOutputStream> playerOutputMap;
    private HashMap<Integer, ClientConnection> playerInputMap;
    private ConcurrentHashMap<Integer, PlayerUser> players;

    private QueryDBForUserDetails db;

    private boolean tableClosed = false;

    /**
     * Constructor for a table object
     * Used for starting game, handling communication with players and adding/removing players
     *
     * @param tableID The ID of the table
     * @param queue Queue object
     */
    public Table(int tableID, Queue queue)
    {
        this.tableID = tableID;
        currentUserID = 1;
        this.queue = queue;
        playerOutputMap = new HashMap<>();
        playerInputMap = new HashMap<>();
        players = new ConcurrentHashMap<>();
        db = new QueryDBForUserDetails();
        System.out.println("Table created with id: " + tableID);
    }

    /**
     * Adds a user and their associated input/output streams to the table and sets their id, updates the game with new states
     *
     * @param user The user to be added
     * @throws IOException
     */
    public void addUserToTable(SocketUser user) throws IOException
    {
        user.getConnection().getOut().writeObject("game_joined");
        System.out.println("sent game joined");
        PlayerUser temp = new PlayerUser(user);

        playerOutputMap.put(currentUserID, user.getConnection().getOut());
        playerInputMap.put(currentUserID, user.getConnection());

        temp.setID(currentUserID);
        sendToUser(temp.getID(), new SetIDCommand(temp.getID()));

        players.put(currentUserID, temp);

        currentUserID++;
        noUsersAtTable++;

        if(game!=null)
        {
            game.updateGamePlayerList(temp);
            game.updateTable(this);
        }

        System.out.println("user added to table: " + temp.username + " \ncurrent users at table = " + players.size());
    }

    /**
     *
     * @return The number of seats left at the table
     */
    public int getOpenSeats()
    {
        return (MAX_USERS - noUsersAtTable);
    }

    /**
     *
     * @return The list of players at the table
     */
    public List<PlayerUser> getPlayers()
    {
        return new ArrayList<>(players.values());
    }

    /**
     * Sends a command to a specified user, and waits for a response if necessary
     *
     * @param id The ID of the user to send to
     * @param command The command to be sent to the user
     * @return A PlayerUserTurn object containing the player's move response and an optional bet
     */
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

    /**
     * Sends a command to all users at the table
     *
     * @param command The command to be sent
     */
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

    /**
     * Removes the player from table hashmaps, decrements the users at table, informs the queue of the removed user and updates the players stats in the database
     * then updates the game with new table state and removes the player from the game too
     *
     * @param id The id of the user to be removed
     */
    public void removeFromTable(int id)
    {
        playerInputMap.remove(id);
        playerOutputMap.remove(id);
        noUsersAtTable--;

        queue.notifyServer(players.get(id));

        db.updateUserDetailsOnChange(game.getPlayer(id));

        players.remove(id);

        game.updateTable(this);
        game.removePlayer(id);
    }

    /**
     * Starts a game thread and then monitors the number of users at the table
     */
    @Override
    public void run() {
        while(true)
        {
            game = new GameRunnable(this);
            Thread gameThread = new Thread(game, "game " + tableID);
            gameThread.start();
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

    /**
     * Required method to be used as a priority queue
     * @param otherTable Table to be compared to
     * @return Comparison value
     */
    @Override
    public int compareTo(Table otherTable) {
        return (this.noUsersAtTable - otherTable.noUsersAtTable);
    }

    /**
     * Closes the current table, ending the thread of execution
     */
    public void closeTable()
    {
        tableClosed = true;
    }

    /**
     * Checks the game object to determine whether the game thread is still running or not
     *
     * @return True if the game has ended, false if not
     */
    public boolean getEndGameFromTable()
    {
        if(game != null) {
            return game.getEndGame();
        }
        return false;
    }
}
