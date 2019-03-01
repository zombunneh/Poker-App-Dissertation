package com.game.poker.psymw6mobilepokerapp.PokerAppServer;

import com.game.poker.psymw6mobilepokerapp.PokerAppDatabase.QueryDBForUserDetails;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.GameUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.Queue;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.User;

import java.io.*;
import java.net.Socket;

public class ServerRunnable implements Runnable {

    private ClientConnection connection;
    private Socket clientSocket = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private QueryDBForUserDetails queryDB = new QueryDBForUserDetails();
    private GameUser user = null;
    private User newUser = null;
    private Queue queue;

    public ServerRunnable(ClientConnection clientSocket, Queue queue)
    {
        this.connection = clientSocket;
        this.clientSocket = clientSocket.getClient();
        this.in = clientSocket.getIn();
        this.out = clientSocket.getOut();
        this.queue = queue;
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
        String requestType = "default";
        while(clientSocket!= null) {
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

                        } else {
                            out.writeBoolean(accountExists);
                            out.flush();
                            System.out.println("waiting for client response");
                            String username = (String) in.readObject();

                            registerNewUser(username, user_id, getAccountType);
                            //handle creating account
                            //now need to somehow do something
                            sendUserLoginDetails(out, user_id, getAccountType);

                        }
                        requestType = "default";
                        break;
                    }
                    case "join_queue": {
                        newUser = new User(user.user_id, user.currency, user.username);
                        //if user clicks play
                        addUserToQueue(newUser);
                        out.writeObject("queue_joined");
                        requestType = "default";
                        break;
                    }
                    case "default":
                        break;
                    default:
                        break;
                }
            } catch (ClassNotFoundException c) {
                System.out.println("exception: " + c.toString());
            }
        }

        while(true)
        {

        }


        //constructs user object to be used for com.game play
        //newUser = new User(user.user_id, user.currency, user.username);
        //if user clicks play
        //addUserToQueue(newUser);
    }

    public void sendUserLoginDetails(ObjectOutputStream out, String user_id, int accountType) throws IOException
    {
        user = queryDB.queryUserTableAndDetailsOnLogin(user_id, accountType);
        out.writeObject(user);
        System.out.println("user details: ");
        System.out.println(user.username);
        System.out.println(user.currency);
        System.out.println(user.hands_played);
        System.out.println("sent object");
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

    public void addUserToQueue(User user)
    {
        queue.addToQueue(connection, user, out, in);
    }
}
