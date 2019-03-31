package com.game.poker.psymw6mobilepokerapp.PokerAppDatabase;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.GameUser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;


public class QueryDBForUserDetails extends SQLDatabaseConnection {
    private String username;
    private Timestamp lastLogin;
    private int currency;
    private int login_streak;
    private int hands_played;
    private int hands_won;
    private int win_rate;
    private int max_winnings;
    private int max_chips;
    private String google_user_id;
    public static final String GOOGLE_CLIENT_ID = "992597545265-dd7gbt39dp4i9iakn1ts1541vc0qebn9.apps.googleusercontent.com";

    /**
     * Query the user's initial details when they login to the app
     * will return from the first table, verifying the user's user_id from auth token, and retrieving their user information
     *
     * @param user_id The user id to query
     * @param accountType 0 is a google account, 1 is a guest account
     * @return A GameUser Object containing the user's database information
     */
    public GameUser queryUserTableAndDetailsOnLogin(String user_id, int accountType)
    {
        username = "null";
        lastLogin = new Timestamp(System.currentTimeMillis());
        currency = 0;
        hands_played = 0;
        hands_won = 0;
        win_rate = 0;
        max_winnings = 0;
        max_chips = 0;
        login_streak = 0;

        if(accountType == 0)
        {
            try {
                ResultSet rs = createSQLStatement("SELECT * FROM users JOIN details ON users.user_id = details.user_id " +
                                "WHERE users.google_user_id ='" + google_user_id +"'",
                        0);
                populateGameUser(rs);
            }
            catch(SQLException e)
            {
                System.err.println("Error querying user table");
                e.printStackTrace();
            }

            disconnectFromDatabase();
            return new GameUser(returnStringTimestamp(lastLogin), google_user_id, currency, login_streak, username, hands_played, hands_won, win_rate, max_winnings, max_chips);
        }
        else
        {
            try {
                ResultSet rs = createSQLStatement("SELECT * FROM users JOIN details ON users.user_id = details.user_id " +
                                "WHERE users.guest_user_id ='" + user_id + "'",
                        0);
                populateGameUser(rs);
            }
            catch(SQLException e)
            {
                System.err.println("Error querying user table");
                e.printStackTrace();
            }

            disconnectFromDatabase();
            return new GameUser(returnStringTimestamp(lastLogin), user_id, currency, login_streak, username, hands_played, hands_won, win_rate, max_winnings, max_chips);
        }
    }

    /**
     * Retrieves user details with the provided id
     *
     * @param id The id of the user to retrieve
     * @return A GameUser containing the latest
     */
    public GameUser queryUserDetailsOnCall(String id)
    {
        boolean googleID = doesUserExistWithGoogle(id, 0);
        if(googleID)
        {
            try {
                ResultSet rs = createSQLStatement("SELECT * FROM users JOIN details ON users.user_id = details.user_id " +
                                "WHERE users.google_user_id ='" + id +"'",
                        0);
                populateGameUser(rs);
            }
            catch(SQLException e)
            {
                System.err.println("Error querying user table");
                e.printStackTrace();
            }

            disconnectFromDatabase();
            return new GameUser(returnStringTimestamp(lastLogin), id, currency, login_streak, username, hands_played, hands_won, win_rate, max_winnings, max_chips);
        }
        else
        {
            if(doesUserExist(id, 1))
            {
                try {
                    ResultSet rs = createSQLStatement("SELECT * FROM users JOIN details ON users.user_id = details.user_id " +
                                    "WHERE users.guest_user_id ='" + id + "'",
                            0);
                    populateGameUser(rs);
                }
                catch(SQLException e)
                {
                    System.err.println("Error querying user table");
                    e.printStackTrace();
                }

                disconnectFromDatabase();
                return new GameUser(returnStringTimestamp(lastLogin), id, currency, login_streak, username, hands_played, hands_won, win_rate, max_winnings, max_chips);
            }
        }
        return null;
    }

    /**
     * If the google user id supplied doesn't already have a linked account update the database to link the google id with the guest account
     *
     * @param user_id The user id token from client
     * @param guest_id The guest id of the existing account
     * @return True if account linking is successfully, false if not
     */
    public boolean attemptAccountLink(String user_id, String guest_id)
    {
        verifyIdRetrieveDetails(user_id);
        boolean accAlreadyExists = doesUserExist(user_id, 0);
        if(accAlreadyExists)
        {
           return false;
        }
        else
        {
           createSQLStatement("UPDATE users SET google_user_id = '" + google_user_id +  "' WHERE guest_user_id = '" + guest_id + "'", 1);
           return true;
        }
    }

    /**
     * Updates the database with new details and stats of the user
     *
     * @param user The user containing the information to update
     */
    public void updateUserDetailsOnChange(PlayerUser user)
    {
        String username = user.username;
        String user_id = user.user_id;
        int currency = user.getCurrency();

        int hands_played = user.hands_played;
        int hands_won = user.hands_won;
        int win_rate = user.win_rate;
        int max_winnings = user.max_winnings;
        int max_chips = user.max_chips;

        if(doesUserExistWithGoogle(user_id, 0))
        {
            createSQLStatement("UPDATE users SET currency = '" + currency +  "' WHERE google_user_id = '" + user_id + "'", 1);
            int id = 0;
            try
            {
                ResultSet rs = createSQLStatement("SELECT user_id FROM users " +
                                "WHERE google_user_id='" + user_id + "'",
                        0);
                if(rs.first())
                {
                    id = rs.getInt("user_id");
                }
            } catch(SQLException e)
            {
                e.printStackTrace();
            }

            createSQLStatement("UPDATE details SET hands_played = '" + hands_played + "', " +
                    "hands_won = '" + hands_won + "', " +
                    "win_rate = '" + win_rate + "', " +
                    "max_winnings ='" + max_winnings + "', " +
                    "max_chips = '" + max_chips + "' " +
                    "WHERE user_id = '" + id + "'", 1);
            System.out.println("update complete");
        }
        else if(doesUserExistWithGoogle(user_id, 1))
        {
            createSQLStatement("UPDATE users SET currency = '" + currency +  "' WHERE guest_user_id = '" + user_id + "'", 1);
            int id = 0;
            try
            {
                ResultSet rs = createSQLStatement("SELECT user_id FROM users " +
                                "WHERE guest_user_id='" + user_id + "'",
                        0);
                if(rs.first())
                {
                    id = rs.getInt("user_id");
                }
            } catch(SQLException e)
            {
                e.printStackTrace();
            }

            createSQLStatement("UPDATE details SET hands_played = '" + hands_played + "', " +
                    "hands_won = '" + hands_won + "', " +
                    "win_rate = '" + win_rate + "', " +
                    "max_winnings ='" + max_winnings + "', " +
                    "max_chips = '" + max_chips + "' " +
                    "WHERE user_id = '" + id + "'", 1);
            System.out.println("update complete");
        }
    }

    /**
     * Updates the database with new details and stats of the user
     *
     * @param user The user containing the information to update
     */
    public void updateUserDetailsOnChange(GameUser user)
    {
        String username = user.username;
        String user_id = user.user_id;
        int currency = user.currency;

        int hands_played = user.hands_played;
        int hands_won = user.hands_won;
        int win_rate = user.win_rate;
        int max_winnings = user.max_winnings;
        int max_chips = user.max_chips;

        if(doesUserExistWithGoogle(user_id, 0))
        {
            createSQLStatement("UPDATE users SET currency = '" + currency +  "' WHERE google_user_id = '" + user_id + "'", 1);
            int id = 0;
            try
            {
                ResultSet rs = createSQLStatement("SELECT user_id FROM users " +
                                "WHERE google_user_id='" + user_id + "'",
                        0);
                if(rs.first())
                {
                    id = rs.getInt("user_id");
                }
            } catch(SQLException e)
            {
                e.printStackTrace();
            }

            createSQLStatement("UPDATE details SET hands_played = '" + hands_played + "', " +
                    "hands_won = '" + hands_won + "', " +
                    "win_rate = '" + win_rate + "', " +
                    "max_winnings ='" + max_winnings + "', " +
                    "max_chips = '" + max_chips + "' " +
                    "WHERE user_id = '" + id + "'", 1);
            System.out.println("update complete");
        }
        else if(doesUserExistWithGoogle(user_id, 1))
        {
            createSQLStatement("UPDATE users SET currency = '" + currency +  "' WHERE guest_user_id = '" + user_id + "'", 1);
            int id = 0;
            try
            {
                ResultSet rs = createSQLStatement("SELECT user_id FROM users " +
                                "WHERE guest_user_id='" + user_id + "'",
                        0);
                if(rs.first())
                {
                    id = rs.getInt("user_id");
                }
            } catch(SQLException e)
            {
                e.printStackTrace();
            }

            createSQLStatement("UPDATE details SET hands_played = '" + hands_played + "', " +
                    "hands_won = '" + hands_won + "', " +
                    "win_rate = '" + win_rate + "', " +
                    "max_winnings ='" + max_winnings + "', " +
                    "max_chips = '" + max_chips + "' " +
                    "WHERE user_id = '" + id + "'", 1);
            System.out.println("update complete");
        }
    }

    /**
     * Checks if the current date is the same day or a new day to the previous login
     * If it is a new day then checks if it is the next day and if it is increments the login streak counter
     * If it is a new day but not the next day the login streak counter is reset to 1
     *
     * @param date The date to be checked
     * @param user_id The user id to be checked
     * @param login_streak The current login streak of the user
     * @return 0 if the user's last login was the same day, otherwise an int representing the current login streak
     */
    public int updateLastLogin(String date, String user_id, int login_streak)
    {
        Date date1 = new Date();
        Timestamp ts = new Timestamp(date1.getTime());

        LocalDate localDate = LocalDate.now();

        String newDate = date.substring(0, 10);

        boolean isNewDay = localDate.isAfter(LocalDate.parse(newDate));
        boolean isNextDay = false;
        if(isNewDay)
        {
            isNextDay = localDate.isBefore(LocalDate.parse(newDate).plusDays(2));
        }

        System.out.println(isNewDay);

        if(doesUserExistWithGoogle(user_id, 0))
        {
            createSQLStatement("UPDATE users SET last_login = '" + ts + "' WHERE google_user_id = '" + user_id + "'", 1);
            if(isNextDay)
            {
                login_streak += 1;
                createSQLStatement("UPDATE users SET login_streak = '" + login_streak + "' WHERE google_user_id = '" + user_id + "'", 1);
                System.out.println("IS NEXT DAY");
            }
            else if(isNewDay && !isNextDay)
            {
                login_streak = 1;
                createSQLStatement("UPDATE users SET login_streak = '" + login_streak + "' WHERE google_user_id = '" + user_id + "'", 1);
            }
            System.out.println("update complete");
        }
        else if(doesUserExistWithGoogle(user_id, 1))
        {
            createSQLStatement("UPDATE users SET last_login = '" + ts +  "' WHERE guest_user_id = '" + user_id + "'", 1);
            if(isNextDay)
            {
                login_streak += 1;
                createSQLStatement("UPDATE users SET login_streak = '" + login_streak + "' WHERE guest_user_id = '" + user_id + "'", 1);
                System.out.println("IS NEXT DAY");
            }
            else if(isNewDay && !isNextDay)
            {
                login_streak = 1;
                createSQLStatement("UPDATE users SET login_streak = '" + login_streak + "' WHERE guest_user_id = '" + user_id + "'", 1);
            }
            System.out.println("update complete");
        }
        System.out.println(login_streak + " = current login streak");
        if(!isNewDay)
        {
            login_streak = 0;
        }
        return login_streak;
    }

    /**
     * Adds a new user to the database using the supplied parameters
     *
     * @param username The username of the user to register
     * @param user_id The user's user id
     * @param accountType 0 is google account, 1 is guest account
     */
    public void addNewUserOnRegister(String username, String user_id, int accountType)
    {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        int id = 0;
        int initialCurrency = 100000;
        int login_streak = 1;

        if(accountType == 0)
        {
            //insert into users table the retrieved details
            createSQLStatement("INSERT INTO users (google_user_id, user_name, last_login, currency, login_streak) " +
                    "VALUES ('" + google_user_id + "', '"  + username + "', '" + ts + "', '" + initialCurrency + "', '" + login_streak + "')", 1);

            //get the id of column corresponding to google user id
            try
            {
                ResultSet rs = createSQLStatement("SELECT user_id FROM users " +
                                "WHERE google_user_id='" + google_user_id + "'",
                        0);
                if(rs.first())
                {
                    id = rs.getInt("user_id");
                }
            } catch(SQLException e)
            {
                e.printStackTrace();
            }

            //insert into details table using retrieved id
            createSQLStatement("INSERT INTO details (user_id, hands_played, hands_won, win_rate, max_winnings, max_chips) " +
                    "VALUES ('" + id + "', '0', '0', '0', '0', '" + initialCurrency + "')", 1);
        }
        else
        {
            //insert into users table the retrieved details
            createSQLStatement("INSERT INTO users (guest_user_id, user_name, last_login, currency, login_streak) " +
                    "VALUES ('" + user_id + "', '"  + username + "', '" + ts + "', '" + initialCurrency + "', '" + login_streak + "')", 1);

            //get the id of column corresponding to guest user id
            try
            {
                ResultSet rs = createSQLStatement("SELECT user_id FROM users " +
                                "WHERE guest_user_id='" + user_id + "'",
                        0);
                if(rs.first())
                {
                    id = rs.getInt("user_id");
                }
            } catch(SQLException e)
            {
                e.printStackTrace();
            }

            //insert into details table using retrieved id
            createSQLStatement("INSERT INTO details (user_id, hands_played, hands_won, win_rate, max_winnings, max_chips) " +
                    "VALUES ('" + id + "', '0', '0', '0', '0', '" + initialCurrency + "')", 1);
        }
    }

    /**
     * Checks if a user entry for the supplied user id exists in the database
     *
     * @param user_id The user id to check
     * @param accountType 0 for a google account, 1 for a guest account
     * @return True if the user is in the database, false if not
     */
    public boolean doesUserExist(String user_id, int accountType)
    {
            ResultSet rs;
            if(accountType == 0)
            {
                rs = createSQLStatement("SELECT * FROM users " +
                        "WHERE google_user_id ='" + google_user_id + "'",
                        0);
            }
            else
            {
                rs = createSQLStatement("SELECT * FROM users " +
                        "WHERE guest_user_id ='" + user_id + "'",
                        0);
            }

            try
            {
                if(rs.next())
                {
                    disconnectFromDatabase();
                    System.out.println("database entry found");
                    return true;
                }
                else
                {
                    disconnectFromDatabase();
                    System.out.println("database entry not found");
                    return false;
                }
            } catch (SQLException e)
            {
                e.printStackTrace();
            }

            return false;
    }

    /**
     * An alternate check for a user that uses the actual google id token instead of client id
     *
     * @param user_id The user id token that is stored in User objects
     * @param accountType Should always be 0 for google account
     * @return True if user is in the database, false if not
     */
    public boolean doesUserExistWithGoogle(String user_id, int accountType)
    {
        ResultSet rs;
        if(accountType == 0)
        {
            rs = createSQLStatement("SELECT * FROM users " +
                            "WHERE google_user_id ='" + user_id + "'",
                    0);
        }
        else
        {
            rs = createSQLStatement("SELECT * FROM users " +
                            "WHERE guest_user_id ='" + user_id + "'",
                    0);
        }

        try
        {
            if(rs.next())
            {
                disconnectFromDatabase();
                System.out.println("database entry found");
                return true;
            }
            else
            {
                disconnectFromDatabase();
                System.out.println("database entry not found");
                return false;
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Uses google api library to retrieve the correct id token for a given user id sent from the client
     *
     * @param user_id The client id to retrieve id token for
     * @return True if successfully retrieved token, false if not
     */
    public boolean verifyIdRetrieveDetails(String user_id)
    {
        HttpTransport transport = new NetHttpTransport();
        JsonFactory factory = new JacksonFactory();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, factory)
                .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                .build();
        System.out.println("verifying user id");
        try
        {
            GoogleIdToken id = verifier.verify(user_id);
            if(id != null)
            {
                Payload payload = id.getPayload();
                google_user_id = payload.getSubject();
                System.out.println("user id is; " + google_user_id);
                return true;
            }
            else
            {
                //TODO handle invalid token
                System.out.println("invalid token");
                return false;
            }
        }
        catch(GeneralSecurityException | IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Converts a timestamp to a string of specified format
     *
     * @param ts The timestamp to convert
     * @return The converted timestamp as a string
     */
    public String returnStringTimestamp(Timestamp ts)
    {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm aaa").format(ts);
    }

    /**
     * Populates the GameUser object with details retrieved from the database
     *
     * @param rs The result set containing the information to add to the user
     * @throws SQLException
     */
    private void populateGameUser(ResultSet rs) throws SQLException
    {
        if(rs.first())
        {
            username = rs.getString("user_name");
            lastLogin = rs.getTimestamp("last_login");
            currency = rs.getInt("currency");
            login_streak = rs.getInt("login_streak");
            hands_played = rs.getInt("hands_played");
            hands_won = rs.getInt("hands_won");
            win_rate = rs.getInt("win_rate");
            max_winnings = rs.getInt("max_winnings");
            max_chips = rs.getInt("max_chips");
        }
    }
}