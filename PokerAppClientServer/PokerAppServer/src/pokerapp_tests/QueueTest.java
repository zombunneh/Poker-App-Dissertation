import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.User;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.Queue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class QueueTest {

    private static Queue queue;
    private static Socket client;
    private static Socket client2;

    @BeforeAll
    public static void init()
    {
        queue = new Queue();
        new Thread(queue).start();
        client = new Socket();
    }

    @DisplayName("Test adding users to queue")
    @Test
    public void addToQueueTest()
    {
        User user = new User("asd", 100, "zombunny");
        //queue.addToQueue(client, user);
    }


    @DisplayName("Test creating a game table")
    @Test
    public void testTableCreation()
    {
        User testUser = new User("asd", 100, "zombunny");
        //queue.addToQueue(client, testUser);
        User testUser2 = new User("asd", 100, "zombunny");
        //queue.addToQueue(client, testUser2);
    }

}