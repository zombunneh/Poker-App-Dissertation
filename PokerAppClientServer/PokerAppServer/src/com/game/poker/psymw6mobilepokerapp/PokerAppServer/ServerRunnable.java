package com.game.poker.psymw6mobilepokerapp.PokerAppServer;

import com.game.poker.psymw6mobilepokerapp.PokerAppDatabase.QueryDBForUserDetails;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.GameUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppRunnable.Queue;

import java.io.*;
import java.net.Socket;

public class ServerRunnable implements Runnable {

    private ClientConnection connection;
    private Socket clientSocket = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private QueryDBForUserDetails queryDB = new QueryDBForUserDetails();
    private GameUser user;
    private Queue queue;

    private final static int BASE_BONUS = 100000;

    private boolean inGame = false;

    /**
     * Constructor for serverRunnable object
     * @param clientSocket
     * @param queue
     */
    public ServerRunnable(ClientConnection clientSocket, Queue queue)
    {
        if(clientSocket != null && queue != null)
        {
            this.connection = clientSocket;
            this.clientSocket = clientSocket.getClient();
            this.in = clientSocket.getIn();
            this.out = clientSocket.getOut();
            this.queue = queue;
        }
    }

    /**
     *  Run method for handling user connections
     */
    @Override
    public void run() {
        try
        {
            handleUserConnection(out, in);
        } catch(IOException e)
        {
            e.printStackTrace();
            clientSocket = null;
        }

    }

    /**
     * Handles the connection communication protocol with the client using a switch based on the request sent as a string by the client
     * Currently handles retrieving the user's account for login, adding the user to queue, and retrieving an updated version of the user's profile
     *
     * @param out The ObjectOutputStream to be used
     * @param in The ObjectInputStream to be used
     * @throws IOException
     */
    public void handleUserConnection(ObjectOutputStream out, ObjectInputStream in) throws IOException
    {
        String requestType;
        while(clientSocket != null) {
            while(!inGame) {
                clientSocket.setSoTimeout(0);
                out.flush();
                try {
                    requestType = (String) in.readObject();

                    switch (requestType) {
                        case "get_account": {
                            int getAccountType = in.readInt();

                            String user_id = (String) in.readObject();

                            //retrieve id token information if google sign in
                            if (getAccountType == 0) {
                                if (!verifyId(user_id)) {
                                    clientSocket.close();
                                }
                            }

                            boolean accountExists = checkAccount(user_id, getAccountType);
                            if (accountExists) {
                                out.writeBoolean(accountExists);

                                sendUserLoginDetails(out, user_id, getAccountType);
                                out.flush();
                            } else {
                                out.writeBoolean(accountExists);
                                out.flush();

                                String username = (String) in.readObject();

                                registerNewUser(username, user_id, getAccountType);
                                //handle creating account
                                sendUserLoginDetails(out, user_id, getAccountType);
                                out.flush();
                            }
                            break;
                        }
                        case "join_queue": {
                            addUserToQueue(user);
                            inGame = true;
                            break;
                        }
                        case "retrieve_profile": {
                            retrieveProfile(user.user_id, out);
                            break;
                        }
                        case "link_account": {
                            String user_id = (String) in.readObject();
                            String guest_id = (String) in.readObject();

                            linkGoogleAccount(user_id, guest_id, out);
                            break;
                        }
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

    /**
     * Constructs the user object from the queryDB object which retrieves the user's details from the database
     * Checks the user's last login time and updates the user's current login streak and gives the appropriate amount of currency if required
     * Writes the user object to the client
     *
     * @param out
     * @param user_id The user id of the account to be retrieved
     * @param accountType Whether the account is a google account or a guest account
     * @throws IOException
     */
    public void sendUserLoginDetails(ObjectOutputStream out, String user_id, int accountType) throws IOException
    {
        user = queryDB.queryUserTableAndDetailsOnLogin(user_id, accountType);
        checkLastLogin();
        out.writeObject(user);
        user.loginStreakChanged = false;
    }

    /**
     *  Checks the users last login time and updates appropriate data
     */
    public void checkLastLogin()
    {
        int currentStreak = queryDB.updateLastLogin(user.lastLogin, user.user_id, user.loginStreak);

        if(currentStreak == 0)
        {

        }
        else
        {
            double multiplier = (1.0) + ((currentStreak / 10.0) - 0.1);
            user.currency += (BASE_BONUS * multiplier);
            user.loginStreak = currentStreak;
            user.loginStreakChanged = true;
            queryDB.updateUserDetailsOnChange(user);
        }

    }

    /**
     * Checks if the account with the corresponding user id exists in the database of users
     *
     * @param user_id The user id of the account to be retrieved
     * @param accountType Whether the account is a google account or a guest account
     * @return True if the account exists, false if it does not
     */
    public boolean checkAccount(String user_id, int accountType)
    {
        return queryDB.doesUserExist(user_id, accountType);
    }

    /**
     * Retrieves the corresponding google account token from a client user id
     *
     * @param user_id The user id of the account to be retrieved
     * @return True if token retrieval was successful, false if not
     */
    public boolean verifyId(String user_id)
    {
        return queryDB.verifyIdRetrieveDetails(user_id);
    }

    /**
     * Registers a new user with the database
     *
     * @param username
     * @param user_id The user id of the account to be retrieved
     * @param accountType Whether the account is a google account or a guest account
     *
     * @throws IOException
     */
    public void registerNewUser(String username, String user_id, int accountType) throws IOException
    {
        queryDB.addNewUserOnRegister(username, user_id, accountType);
    }

    /**
     * Retrieves updated user details and sends them to client
     *
     * @param id The user's id
     * @param out The output stream
     * @throws IOException
     */
    public void retrieveProfile(String id, ObjectOutputStream out) throws IOException
    {
        user = queryDB.queryUserDetailsOnCall(id);
        out.writeObject(user);
    }

    /**
     *
     */
    public void linkGoogleAccount(String user_id, String guest_id, ObjectOutputStream out) throws IOException
    {
        boolean linkResult = queryDB.attemptAccountLink(user_id, guest_id);
        if(linkResult)
        {
            out.writeObject("account_linked");
        }
        else
        {
            out.writeObject("account_link_fail");
        }
    }

    /**
     * Adds a user into the queue object
     *
     * @param user The user to be added to the queue
     */
    public void addUserToQueue(GameUser user)
    {
        queue.addToQueue(connection, user, this);
    }

    /**
     * Called when a player leaves their game in order to resume communication with this object
     */
    public void leftGame()
    {
        inGame = false;
    }

    /**
     *
     * @return The current user's id as a string
     */
    public String getUserID()
    {
        return user.user_id;
    }
}
