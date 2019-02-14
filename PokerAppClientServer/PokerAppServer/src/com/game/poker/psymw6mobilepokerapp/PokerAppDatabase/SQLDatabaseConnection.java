package com.game.poker.psymw6mobilepokerapp.PokerAppDatabase;

import java.rmi.ServerError;
import java.sql.*;

public class SQLDatabaseConnection {
    private static final String dburl = "jdbc:mysql://localhost:3306/pokerappdatabase?useSSL=false&allowPublicKeyRetrieval=true";
    public static final String username = "matthew";
    public static final String password = "123465";
    //restructured database here
    private Connection connect;

    //establish connection to database storing com.game.poker app user's details
    public Connection connectToDatabase(String url, String user, String pass)
    {
       try
       {
           System.out.println("The url is " + url);
           Connection connection = DriverManager.getConnection(url, user, pass);
           System.out.println("connected to database");
           return connection;
       }
       catch(SQLException e)
       {
           System.err.println("Error connecting to database");
           e.printStackTrace();
       }
       return null;
    }

    /*
        Return a ResultSet object containing result of SQL query to database
        or return null if executing an update to database, denoted by @param queryType 0 is a query 1 is an update
    */
    public ResultSet createSQLStatement(String statement, int queryType)
    {
        try {
            connect = connectToDatabase(dburl, username, password);
            Statement st = connect.createStatement();
            if(queryType == 0)
            {
                return st.executeQuery(statement);
            }
            else if(queryType == 1)
            {
                st.executeUpdate(statement);
                return null;
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error creating SQL statement");
            e.printStackTrace();
        }
        return null;
    }


    //close the database connection when it is no longer needed
    public void disconnectFromDatabase()
    {
        try{
            connect.close();
        }
        catch(SQLException e)
        {
            System.err.println("Error disconnecting from database");
            e.printStackTrace();
        }

    }
}
