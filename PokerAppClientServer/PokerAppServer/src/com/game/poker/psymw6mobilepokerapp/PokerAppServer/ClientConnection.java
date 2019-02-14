package com.game.poker.psymw6mobilepokerapp.PokerAppServer;


import java.io.IOException;
import java.net.Socket;

public class ClientConnection {
    private static final int TIMEOUT = 30;
    private Socket client;

    public ClientConnection(Socket client)
    {
        this.client = client;
    }

    public Socket getClient()
    {
        return client;
    }
}
//potentially to be used for in com.game communication