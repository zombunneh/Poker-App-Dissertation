
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.GameUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppServer.SocketConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

class SocketConnectionTest {

    private static SocketConnection serverConnection;
    private static ServerSocket server;

    @BeforeAll
    public static void init() throws IOException
    {

        /*serverConnection = new SocketConnection();
        server = serverConnection.createServerSocket();*/
        //serverConnection.listenForClientConnection(server);
        //QueryDBForUserDetails db = new QueryDBForUserDetails();
        //System.out.println(db.queryUserTableOnLogin(1111));
    }

    @Test
    public void mockTest()
    {
        //used for testing meta information
    }

    //test passes as long as exception not thrown, i.e connection is accepted by server
    @DisplayName("Test connecting a single client to server socket")
    @Test
    public void testSingleSocket()
    {
        System.out.println("Initialising client socket");
        try {
            Socket clientSocket = new Socket(InetAddress.getLocalHost(), SocketConnection.port);
            clientSocket.close();
        }
        catch(IOException e)
        {
            fail("IOException thrown, server connection not established");
        }
        System.out.println("Client socket connected to server successfully");
    }

    //test passes as long as exception not thrown, i.e connection rejected
    @DisplayName("Test connecting multiple clients to server socket and requesting data")
    @Test
    public void testMultipleSocket()
    {
        System.out.println("Initialising multiple client sockets");
        try {
            Socket clientSocket = new Socket(InetAddress.getLocalHost(), SocketConnection.port);
            Socket clientSocket2 = new Socket(InetAddress.getLocalHost(), SocketConnection.port);
            clientSocket.close();
            clientSocket2.close();
        }
        catch(IOException e)
        {
            fail("IOException thrown, server connection not established");
        }
        System.out.println("Client sockets connected to server successfully");
    }

    //test passes as long as user data is retrieved, fails on IOException,
    //but will hang infinitely if server does not have proper communication protocol set up
    @DisplayName("Test retrieval of user data after establishing server connection")
    @Test
    public void testUserRetrieval()
    {
        String username = "null";
        String lastLogin = "null";
        int currency = 0;

        try {
            Socket newSocket =  new Socket(InetAddress.getLocalHost(), SocketConnection.port);

            ObjectOutputStream out = new ObjectOutputStream(newSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(newSocket.getInputStream());

            System.out.println("sending server request");
            out.writeBoolean(true);
            out.writeInt(1111);
            out.flush();
            System.out.println("reading server response");
            GameUser user = (GameUser) in.readObject();

            username = user.username;
            lastLogin = user.lastLogin;
            currency = user.currency;

            newSocket.close();
        }
        catch(IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
            fail("IOException thrown");
        }

        assertEquals(username,"thomas1");
        assertEquals(lastLogin, "2018-03-12 02:06 am");
        assertEquals(currency, 670000);

        System.out.println(String.format(Locale.getDefault(), "%s, %s, %d", username, lastLogin, currency));
    }

}