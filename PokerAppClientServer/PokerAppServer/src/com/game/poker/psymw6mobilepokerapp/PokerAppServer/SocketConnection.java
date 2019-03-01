package com.game.poker.psymw6mobilepokerapp.PokerAppServer;

import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.Queue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketConnection {

    //creating server socket object and port constant
    private static ServerSocket server;
    public final static int port = 4567;
    private boolean isServerStopped = false;
    private ClientConnection connection = null;

    public static void main(String[] args) throws IOException {
        SocketConnection myConnection = new SocketConnection();
        server = myConnection.createServerSocket();
        myConnection.listenForClientConnection(server);

    }

    //initialise a server socket
    public ServerSocket createServerSocket() throws IOException
    {
        return new ServerSocket(port);
    }

    //client handling
    public void listenForClientConnection(ServerSocket server)
    {
        System.out.println("hello from listenForClientConnection");
        //start queue thread
        Queue queue = new Queue();
        new Thread(queue, "queue thread").start();
        Socket clientSocket = null;
         while(!isServerStopped)
            {
                try
                {
                    System.out.println("created server socket o/");
                    clientSocket = server.accept();
                    connection = new ClientConnection(clientSocket);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                new Thread( new ServerRunnable(connection, queue)).start();
            }
         queue.shutdownThread(true);
    }
}
