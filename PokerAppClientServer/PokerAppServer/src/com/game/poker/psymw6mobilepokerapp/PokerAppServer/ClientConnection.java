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

    /**
     * Constructor for a connection object storing the socket and its associated input and output streams
     * @param client The socket to be stored
     * @throws IOException
     */
    public ClientConnection(Socket client) throws IOException
    {
        this.client = client;
        in = new ObjectInputStream(client.getInputStream());
        out = new ObjectOutputStream(client.getOutputStream());
    }

    /**
     *
     * @return The client socket
     */
    public Socket getClient()
    {
        return client;
    }

    /**
     *
     * @return The socket's ObjectInputStream
     */
    public ObjectInputStream getIn()
    {
        return in;
    }

    /**
     *
     * @return The socket's ObjectOutputStream
     */
    public ObjectOutputStream getOut()
    {
        return out;
    }

    /**
     * Waits for a client response
     *
     * @return A PlayerUserTurn object representing the client's response or null if exception is thrown
     * @throws IOException
     */
    public PlayerUserTurn getPlayerMove() throws IOException
    {
        PlayerUserTurn turn;
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