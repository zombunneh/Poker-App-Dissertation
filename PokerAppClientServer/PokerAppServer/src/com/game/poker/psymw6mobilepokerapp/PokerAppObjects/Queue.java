package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.SocketUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.User;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.PriorityBlockingQueue;

public class Queue implements Runnable{
    private PriorityBlockingQueue<SocketUser> userQueue = new PriorityBlockingQueue<>();
    private PriorityBlockingQueue<Table> tablePriorityQueue = new PriorityBlockingQueue<>();
    private SocketUser socketUser;
    private Table table;

    private boolean endThread = false;
    //REMEMBER TO CHANGE MIN PLAYERS AFTER TESTING
    private static final int MIN_PLAYERS = 1;

    private static int roomID = 1;

    public void addToQueue(Socket client, User user)
    {
        socketUser = new SocketUser(user.user_id, user.currency, user.username, client);
        userQueue.add(socketUser);
        System.out.println("user added: " + user.user_id + " " + user.username + " number of current users: " + userQueue.size());
    }

    public void addOpenTable(Table table)
    {
        if(!tablePriorityQueue.contains(table))
            tablePriorityQueue.add(table);
        System.out.println("added table " + table.tableID);
    }

    public void removeTable(Table table)
    {
        tablePriorityQueue.remove(table);
    }

    public void removeFromQueue(SocketUser user)
    {
        userQueue.remove(user);
    }

    /*
    while thread is running:
    if table queue is empty and more than min players in queue, create a new table
    else if table queue isnt empty and has open seats, remove first user from list and add them to the table
    and if table has no open seats then remove it from queue
     */
    //TODO START TABLE THREAD U DUMBASS
    @Override
    public void run() {
        System.out.println("Queue thread started");
        while(!endThread)
        {
            if(tablePriorityQueue.peek() == null && userQueue.size() >= MIN_PLAYERS)
            {
                table = new Table(roomID++);
                new Thread(table).start();
                addOpenTable(table);
            }
            else if(tablePriorityQueue.peek() != null)
            {
                if(tablePriorityQueue.peek().getOpenSeats() == 0)
                {
                    tablePriorityQueue.poll();
                }
                else
                {
                    try
                    {
                        tablePriorityQueue.peek().addUserToTable(userQueue.poll());
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("Queue thread ended.");
    }

    public void shutdownThread(boolean close)
    {
        this.endThread = close;
    }

}
//needs to maintain a list of open tables users can be added to
//needs to be able to receive a user object that will be added to the queue with a unique identifier, + the relevant socket