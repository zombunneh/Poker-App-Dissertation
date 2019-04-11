package com.game.poker.psymw6mobilepokerapp.PokerAppServer;

import java.io.IOException;
import java.util.Scanner;

public class ServerMonitor implements Runnable {

    private Scanner scanner;
    private SocketConnection connection;

    /**
     * Class monitors user input and will end the server on request
     */
    public ServerMonitor(SocketConnection connection)
    {
        scanner = new Scanner(System.in);
        this.connection = connection;
    }

    @Override
    public void run() {
        String str = "";
        while(!str.equals("exit")) {
            System.out.println("Input 'exit' to end server");
            str = scanner.nextLine();
            System.out.println(str);
        }
        try {
            connection.closeServer();
            connection.getServer().close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
