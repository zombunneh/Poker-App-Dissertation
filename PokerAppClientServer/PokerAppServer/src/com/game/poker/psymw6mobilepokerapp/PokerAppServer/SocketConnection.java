package com.game.poker.psymw6mobilepokerapp.PokerAppServer;

import com.game.poker.psymw6mobilepokerapp.PokerAppRunnable.Queue;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketConnection {

    //creating server socket object and port constant
    private static ServerSocket server;
    public final static int port = 4567;
    private boolean isServerStopped = false;
    private ClientConnection connection = null;

    /**
     * Run to start server
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        SocketConnection myConnection = new SocketConnection();
        server = myConnection.createServerSocket();
        myConnection.listenForClientConnection(server);

    }

    /**
     * Initialises a server socket
     * @return The server socket to be used
     * @throws IOException
     */
    public ServerSocket createServerSocket() throws IOException
    {
        return new ServerSocket(port);
    }

    /**
     * Start the queue thread and whilst server is running continue accepting client connections and put them into their own thread for handling communication
     * @param server The server socket to be used for accepting connections
     */
    public void listenForClientConnection(ServerSocket server)
    {
        //start queue thread
        Queue queue = new Queue();
        new Thread(queue, "queue thread").start();
        Socket clientSocket = null;
         while(!isServerStopped)
            {
                try
                {
                    clientSocket = server.accept();
                    connection = new ClientConnection(clientSocket);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                new Thread( new ServerRunnable(connection, queue), "server connection").start();
            }
         queue.shutdownThread(true);
    }
}
