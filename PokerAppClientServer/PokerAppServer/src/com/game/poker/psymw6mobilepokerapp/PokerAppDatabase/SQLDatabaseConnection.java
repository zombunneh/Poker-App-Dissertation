package com.game.poker.psymw6mobilepokerapp.PokerAppDatabase;

import java.sql.*;

public class SQLDatabaseConnection {
    private static final String dburl = "jdbc:mysql://localhost:3306/pokerappdatabase?useSSL=false&allowPublicKeyRetrieval=true";
    public static final String username = "matthew";
    public static final String password = "123465";

    private Connection connect;

    /**
     * Establishes connection to the database storing app user's details
     *
     * @param url URL of the database to connect to
     * @param user Username for connecting to the database
     * @param pass Password for connecting to the database
     * @return A connection object that can be used to perform queries on the database or null if an error occurs
     */
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

    /**
     * Creates an SQLStatement from the string passed in and executes it on the database
     *
     * @param statement The sql statement to be executed
     * @param queryType 0 is a regular query, 1 is an update to the database
     * @return A ResultSet object containing result of SQL query to database
     *         or return null if executing an update to database, denoted by queryType
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


    /**
     * Closes the database connection
     */
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
