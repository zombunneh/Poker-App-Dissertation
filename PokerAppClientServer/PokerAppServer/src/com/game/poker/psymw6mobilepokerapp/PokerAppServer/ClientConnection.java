package com.game.poker.psymw6mobilepokerapp.PokerAppServer;


import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUserTurn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection {
    private static final int TIMEOUT = 20000;
    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientConnection(Socket client) throws IOException
    {
        this.client = client;
        in = new ObjectInputStream(client.getInputStream());
        out = new ObjectOutputStream(client.getOutputStream());
    }

    public Socket getClient()
    {
        return client;
    }

    public ObjectInputStream getIn()
    {
        return in;
    }

    public ObjectOutputStream getOut()
    {
        return out;
    }

    //code to receive a client response for a game turn
    public PlayerUserTurn getPlayerMove() throws IOException
    {
        System.out.println("getplayermove");
        PlayerUserTurn turn = null;
        try
        {
            client.setSoTimeout(TIMEOUT);
            turn = (PlayerUserTurn) in.readObject();
            return turn;
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
//potentially to be used for in com.game communication