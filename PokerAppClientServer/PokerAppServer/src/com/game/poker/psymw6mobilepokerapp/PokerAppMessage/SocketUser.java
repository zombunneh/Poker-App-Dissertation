package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

import java.net.Socket;
import java.util.Random;

public class SocketUser extends User implements Comparable<SocketUser>{
    public Socket client;
    private int priority;
    private static final long serialVersionUID = 15489315874512L;

    public SocketUser(String user_id, int currency, String username, Socket clientSocket)
    {
        super(user_id, currency, username);
        this.client = clientSocket;
        this.priority = new Random().nextInt(10);
    }

    @Override
    public int compareTo(SocketUser otherUser) {
        return (this.priority - otherUser.priority);
    }
}
