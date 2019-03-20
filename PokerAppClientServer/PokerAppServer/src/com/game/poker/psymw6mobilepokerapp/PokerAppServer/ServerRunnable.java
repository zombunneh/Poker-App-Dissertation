package com.game.poker.psymw6mobilepokerapp.PokerAppServer;

import com.game.poker.psymw6mobilepokerapp.PokerAppDatabase.QueryDBForUserDetails;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.GameUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.Queue;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.User;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;

public class ServerRunnable implements Runnable {

    private ClientConnection connection;
    private Socket clientSocket = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private QueryDBForUserDetails queryDB = new QueryDBForUserDetails();
    private GameUser user;
    private User newUser;
    private Queue queue;
    private int id;

    private final static int BASE_BONUS = 100000;

    private boolean inGame = false;

    public ServerRunnable(ClientConnection clientSocket, Queue queue)
    {
        if(clientSocket != null && queue != null)
        {
            this.connection = clientSocket;
            this.clientSocket = clientSocket.getClient();
            this.in = clientSocket.getIn();
            this.out = clientSocket.getOut();
            this.queue = queue;
            this.id = 1;
        }
    }

    @Override
    public void run() {
        System.out.println("server runnable started");
        try
        {
            //first client line should be a boolean describing whether retrieving a user account
            //or needing to create a new one
            handleUserConnection(out, in);
        } catch(IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("server runnable ended");
    }

    //TODO remember to implements friends feature, and chat feature
    //designed assuming constant connection, might need to refactor later
    public void handleUserConnection(ObjectOutputStream out, ObjectInputStream in) throws IOException
    {
        String requestType;
        while(clientSocket != null) {
            while(!inGame) {
                System.out.println("serverrunnable");
                clientSocket.setSoTimeout(0);
                out.flush();
                try {
                    requestType = (String) in.readObject();

                    switch (requestType) {
                        case "get_account": {
                            int getAccountType = in.readInt();
                            System.out.println("read into getAccountType");

                            String user_id = (String) in.readObject();
                            System.out.println("read user_id:" + user_id);

                            //retrieve id token information if google sign in
                            if (getAccountType == 0) {
                                if (!verifyId(user_id)) {
                                    //TODO handle telling client id token is invalid
                                    clientSocket.close();
                                }
                            }

                            boolean accountExists = checkAccount(user_id, getAccountType);
                            System.out.println("send bool account exists: " + accountExists);
                            if (accountExists) {
                                out.writeBoolean(accountExists);
                                System.out.println("sending user id details for token: " + user_id);

                                sendUserLoginDetails(out, user_id, getAccountType);
                                out.flush();
                            } else {
                                out.writeBoolean(accountExists);
                                out.flush();
                                System.out.println("waiting for client response");
                                String username = (String) in.readObject();

                                registerNewUser(username, user_id, getAccountType);
                                //handle creating account
                                //now need to somehow do something
                                sendUserLoginDetails(out, user_id, getAccountType);
                                out.flush();
                            }
                            break;
                        }
                        case "join_queue": {
                            System.out.println(user.user_id);
                            addUserToQueue(user);
                            inGame = true;
                            break;
                        }
                        case "retrieve_profile": {

                        }
                            break;
                        case "default":
                            break;
                        default:
                            break;
                    }
                } catch (ClassNotFoundException c) { // need to catch socket exception for crashed/closed clients
                    System.out.println("exception: " + c.toString());
                }
            }
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {

            }
        }
    }

    public void sendUserLoginDetails(ObjectOutputStream out, String user_id, int accountType) throws IOException
    {
        user = queryDB.queryUserTableAndDetailsOnLogin(user_id, accountType);
        checkLastLogin();
        out.writeObject(user);
        user.loginStreakChanged = false;
        System.out.println("user details: ");
        System.out.println(user.username);
        System.out.println(user.currency);
        System.out.println(user.hands_played);
        System.out.println(user.loginStreak);
        System.out.println("sent object");
    }

    public void checkLastLogin()
    {
        int currentStreak = queryDB.updateLastLogin(user.lastLogin, user.user_id, user.loginStreak);

        if(currentStreak == 0)
        {

        }
        else
        {
            double multiplier = (1.0) + ((currentStreak / 10.0) - 0.1);
            System.out.println("current multiplier");
            user.currency += (BASE_BONUS * multiplier);
            user.loginStreak = currentStreak;
            user.loginStreakChanged = true;
            queryDB.updateUserDetailsOnChange(user);
        }

    }

    public boolean checkAccount(String user_id, int accountType)
    {
        return queryDB.doesUserExist(user_id, accountType);
    }

    public boolean verifyId(String user_id)
    {
        return queryDB.verifyIdRetrieveDetails(user_id);
    }

    public void registerNewUser(String username, String user_id, int accountType) throws IOException
    {
        System.out.println("register new user");
        queryDB.addNewUserOnRegister(username, user_id, accountType);
    }

    public void addUserToQueue(GameUser user)
    {
        queue.addToQueue(connection, user, out, in, this);
    }

    public void leftGame()
    {
        inGame = false;
        System.out.println("left game " + user.username);
    }

    public String getUserID()
    {
        return user.user_id;
    }
}
