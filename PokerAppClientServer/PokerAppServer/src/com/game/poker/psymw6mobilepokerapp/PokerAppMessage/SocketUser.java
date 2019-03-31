package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

import com.game.poker.psymw6mobilepokerapp.PokerAppServer.ClientConnection;

import java.util.Random;

public class SocketUser extends User implements Comparable<SocketUser>{
    private ClientConnection connection;
    private int priority;

    public String lastLogin;
    public int hands_played;
    public int hands_won;
    public int win_rate;
    public int max_winnings;
    public int max_chips;

    private static final long serialVersionUID = 15489315874512L;

    /**
     * A subclass of the user object which contains an additional connection object
     *
     * @param user_id The id of the user
     * @param currency The amount of currency the user has
     * @param username The username of the user
     * @param connection The connection object to be stored
     */
    public SocketUser(String user_id, int currency, String username, ClientConnection connection)
    {
        super(user_id, currency, username);
        this.priority = new Random().nextInt(10);
        this.connection = connection;
    }

    /**
     * A subclass of the user object which contains an additional connection object
     *
     * @param user The user to construct a SocketUser out of
     * @param connection The connection object to be stored
     */
    public SocketUser(GameUser user, ClientConnection connection)
    {
        super(user.user_id, user.currency, user.username);
        this.priority = new Random().nextInt(10);
        this.connection = connection;

        this.lastLogin = user.lastLogin;
        this.hands_played = user.hands_played;
        this.hands_won = user.hands_won;
        this.win_rate = user.win_rate;
        this.max_winnings = user.max_winnings;
        this.max_chips = user.max_chips;
    }

    /**
     *
     * @return The stored ClientConnection object
     */
    public ClientConnection getConnection()
    {
        return connection;
    }

    /**
     * Used to determine the order of users in the queue
     *
     * @param otherUser User to compare to
     * @return An int denoting whether the user has higher (1), lower (-1) or equal (0) priority
     */
    @Override
    public int compareTo(SocketUser otherUser) {
        return (this.priority - otherUser.priority);
    }
}
