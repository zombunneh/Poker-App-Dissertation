package com.game.poker.psymw6mobilepokerapp.PokerAppServer;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientConnection {
    private static final int TIMEOUT = 30;
    private Socket client;
    private ObjectInputStream in;

    public ClientConnection(Socket client) throws IOException
    {
        this.client = client;
        in = new ObjectInputStream(client.getInputStream());
    }

    public Socket getClient()
    {
        return client;
    }

    public ObjectInputStream getIn()
    {
        return in;
    }

    //code to receive a client response for a game turn
    public void getPlayerMove()
    {
        try
        {
            client.setSoTimeout(TIMEOUT);
        }
        catch(SocketException e)
        {
            e.printStackTrace();
        }
    }

}
//potentially to be used for in com.game communication