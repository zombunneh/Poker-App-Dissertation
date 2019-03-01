package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

import com.game.poker.psymw6mobilepokerapp.PokerAppServer.ClientConnection;

import java.util.Random;

public class SocketUser extends User implements Comparable<SocketUser>{
    private ClientConnection connection;
    private int priority;
    private static final long serialVersionUID = 15489315874512L;

    public SocketUser(String user_id, int currency, String username, ClientConnection connection)
    {
        super(user_id, currency, username);
        this.priority = new Random().nextInt(10);
        this.connection = connection;
    }

    public ClientConnection getConnection()
    {
        return connection;
    }

    @Override
    public int compareTo(SocketUser otherUser) {
        return (this.priority - otherUser.priority);
    }
}
