package com.game.poker.psymw6mobilepokerapp.PokerAppDatabase;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.GameUser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;


public class QueryDBForUserDetails extends SQLDatabaseConnection {
    private String username;
    private Timestamp lastLogin;
    private int currency;
    private int hands_played;
    private int hands_won;
    private int win_rate;
    private int max_winnings;
    private int max_chips;
    private String google_user_id;
    public static final String GOOGLE_CLIENT_ID = "992597545265-dd7gbt39dp4i9iakn1ts1541vc0qebn9.apps.googleusercontent.com";
    //query the user's initial details when they login to the app
    //will return from the first table, verifying the user's user_id from auth token, and retrieving their username,
    //last login, and currency value
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
            return new GameUser(returnStringTimestamp(lastLogin), google_user_id, currency, username, hands_played, hands_won, win_rate, max_winnings, max_chips);
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
            return new GameUser(returnStringTimestamp(lastLogin), user_id, currency, username, hands_played, hands_won, win_rate, max_winnings, max_chips);
        }
    }

    //will be used to retrieve updated statistic of the user when necessary
    public void queryUserDetailsOnCall()
    {

    }

    //update user table
    //TODO overload with different parameter options
    public void updateUserTableOnChange()
    {

    }

    //
    public void updateUserDetailsOnChange()
    {

    }

    //add new users to database
    public void addNewUserOnRegister(String username, String user_id, int accountType)
    {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        int id = 0;
        if(accountType == 0)
        {
            //insert into users table the retrieved details
            createSQLStatement("INSERT INTO users (google_user_id, user_name, last_login, currency) " +
                    "VALUES ('" + google_user_id + "', '"  + username + "', '" + ts + "', '0')", 1);

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
                    "VALUES ('" + id + "', '0', '0', '0', '0', '0')", 1);
        }
        else
        {
            //insert into users table the retrieved details
            createSQLStatement("INSERT INTO users (guest_user_id, user_name, last_login, currency) " +
                    "VALUES ('" + user_id + "', '"  + username + "', '" + ts + "', '0')", 1);

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
                    "VALUES ('" + id + "', '0', '0', '0', '0', '0')", 1);
        }
    }

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

    //verifies id using token sent to google api library and retrieves actual user id for use in database
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

    public String returnStringTimestamp(Timestamp ts)
    {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm aaa").format(ts);
    }

    private void populateGameUser(ResultSet rs) throws SQLException
    {
        if(rs.first())
        {
            username = rs.getString("user_name");
            lastLogin = rs.getTimestamp("last_login");
            currency = rs.getInt("currency");
            hands_played = rs.getInt("hands_played");
            hands_won = rs.getInt("hands_won");
            win_rate = rs.getInt("win_rate");
            max_winnings = rs.getInt("max_winnings");
            max_chips = rs.getInt("max_chips");
            //TODO retrieve fields from details table, have to populate it first.
        }
    }

}
//build a user using this information to keep data mostly stored online?