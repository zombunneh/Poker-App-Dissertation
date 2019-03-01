import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.GamePlayerList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GamePlayerListTest {
    private static GamePlayerList players;
    private static PlayerUser testUser;
    private static PlayerUser testUser2;
    private static PlayerUser testUser3;

    @BeforeAll
    public static void init()
    {
        players = new GamePlayerList();
        testUser = new PlayerUser("asd", 100, "zombunny");
        testUser2 = new PlayerUser("asd", 100, "zomhunny");
        testUser3 = new PlayerUser("asd", 100, "zomfunny");
        testUser.setID(0);
        testUser2.setID(1);
        testUser3.setID(2);
        players.addPlayer(testUser);
        players.addPlayer(testUser2);
        players.addPlayer(testUser3);
    }

    @Test
    void getPlayers() {
        players.getPlayers();
    }

    @Test
    void getNextPlayer() {
       PlayerUser next = players.getNextPlayer(testUser);
       System.out.println(next.getID());
       assertTrue(next.getID() == testUser2.getID());
    }

    @Test
    void getRandomPlayer() {
        PlayerUser random = players.getRandomPlayer();
        System.out.println(random.username);
    }

    @Test
    void addPlayer() {

    }

    @Test
    void removePlayer() {
        players.removePlayer(testUser.getID());
    }

    @Test
    void getDealer() {
        players.getDealer();
    }

}