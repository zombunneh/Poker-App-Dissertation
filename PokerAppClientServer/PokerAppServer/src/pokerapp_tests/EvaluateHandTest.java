import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.Deck;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.EvaluateHand;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.Hand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EvaluateHandTest {
    private List<Card> communityCards;
    private List<PlayerUser> listPlayers;
    private Deck deck;
    private PlayerUser[] testPlayer = new PlayerUser[3];

    public static final int NUM_OF_TEST_PLAYERS = 3;
    private EvaluateHand evaluator;
    private Card[] fullHand;

    @BeforeEach
    void setUp() {
        deck = new Deck();
        deck.shuffleDeck();
        testPlayer[0] = new PlayerUser("asd", 100, "zombunny");
        testPlayer[1] = new PlayerUser("asdf", 1000, "zomhunny");
        testPlayer[2] = new PlayerUser("asdfg", 10000, "zomfunny");

        listPlayers = new ArrayList<>();
        communityCards = new ArrayList<>();

        Card[] hand;

        for(int i = 0; i < NUM_OF_TEST_PLAYERS; i++)
        {
            hand = new Card[2];
            for(int j = 0; j < hand.length; j++)
            {
                hand[j] = deck.drawCard();
            }
            testPlayer[i].setHand(hand);
            hand = null;
            listPlayers.add(testPlayer[i]);
        }

        for(int i = 0; i < 5; i++)
            communityCards.add(deck.drawCard());

        evaluator = new EvaluateHand();
    }

    @DisplayName("Test sorting of hand algorithm and implementation")
    @Test
    public void testSortHand()
    {
        fullHand = evaluator.createFullHand(testPlayer[0], communityCards);
        evaluator.sortHand(fullHand);
        assertTrue(testOrder(fullHand));
    }

    @DisplayName("Test full EvaluateHand class logic")
    @Test
    public void testEvaluateHand()
    {
        HashMap<PlayerUser, Hand> players;
        players = evaluator.handEvaluator(listPlayers, communityCards);
        evaluator.getHandWinner(players);
    }

    @DisplayName("Test for a royal flush hand")
    @Test
    public void testRoyalFlush()
    {
        Card[] handRoyalFlush = new Card[7];
        handRoyalFlush[0] = new Card(Card.Suit.Clubs, Card.Rank.TEN);
        handRoyalFlush[1] = new Card(Card.Suit.Clubs, Card.Rank.JACK);
        handRoyalFlush[2] = new Card(Card.Suit.Hearts, Card.Rank.JACK);
        handRoyalFlush[3] = new Card(Card.Suit.Spades, Card.Rank.QUEEN);
        handRoyalFlush[4] = new Card(Card.Suit.Clubs, Card.Rank.QUEEN);
        handRoyalFlush[5] = new Card(Card.Suit.Clubs, Card.Rank.KING);
        handRoyalFlush[6] = new Card(Card.Suit.Clubs, Card.Rank.ACE);
        Hand handReturned = evaluator.checkHand(handRoyalFlush);
        System.out.println(handReturned.toString());
        assertTrue(handReturned == Hand.ROYAL_FLUSH);
    }

    @DisplayName("Test for a straight flush hand")
    @Test
    public void testStraightFlush()
    {
        Card[] handStraightFlush = new Card[7];
        handStraightFlush[0] = new Card(Card.Suit.Clubs, Card.Rank.TWO);
        handStraightFlush[1] = new Card(Card.Suit.Clubs, Card.Rank.THREE);
        handStraightFlush[2] = new Card(Card.Suit.Hearts, Card.Rank.THREE);
        handStraightFlush[3] = new Card(Card.Suit.Clubs, Card.Rank.FOUR);
        handStraightFlush[4] = new Card(Card.Suit.Clubs, Card.Rank.FIVE);
        handStraightFlush[5] = new Card(Card.Suit.Diamonds, Card.Rank.FIVE);
        handStraightFlush[6] = new Card(Card.Suit.Clubs, Card.Rank.ACE);
        Hand handReturned = evaluator.checkHand(handStraightFlush);
        System.out.println(handReturned.toString());
        assertTrue(handReturned == Hand.STRAIGHT_FLUSH);
    }

    @DisplayName("Test for a four of a kind hand")
    @Test
    public void testFourOfKind()
    {
        Card[] handFourKind = new Card[7];
        handFourKind[0] = new Card(Card.Suit.Clubs, Card.Rank.TWO);
        handFourKind[1] = new Card(Card.Suit.Clubs, Card.Rank.THREE);
        handFourKind[2] = new Card(Card.Suit.Hearts, Card.Rank.THREE);
        handFourKind[3] = new Card(Card.Suit.Diamonds, Card.Rank.THREE);
        handFourKind[4] = new Card(Card.Suit.Spades, Card.Rank.THREE);
        handFourKind[5] = new Card(Card.Suit.Diamonds, Card.Rank.FIVE);
        handFourKind[6] = new Card(Card.Suit.Clubs, Card.Rank.ACE);
        Hand handReturned = evaluator.checkHand(handFourKind);
        System.out.println(handReturned.toString());
        assertTrue(handReturned == Hand.FOUR_KIND);
    }

    @DisplayName("Test for a fullhouse hand")
    @Test
    public void testFullhouse()
    {
        Card[] handFullhouse = new Card[7];
        handFullhouse[0] = new Card(Card.Suit.Clubs, Card.Rank.TWO);
        handFullhouse[1] = new Card(Card.Suit.Clubs, Card.Rank.THREE);
        handFullhouse[2] = new Card(Card.Suit.Hearts, Card.Rank.THREE);
        handFullhouse[3] = new Card(Card.Suit.Diamonds, Card.Rank.FOUR);
        handFullhouse[4] = new Card(Card.Suit.Spades, Card.Rank.FIVE);
        handFullhouse[5] = new Card(Card.Suit.Diamonds, Card.Rank.FIVE);
        handFullhouse[6] = new Card(Card.Suit.Clubs, Card.Rank.FIVE);
        Hand handReturned = evaluator.checkHand(handFullhouse);
        System.out.println(handReturned.toString());
        assertTrue(handReturned == Hand.FULL_HOUSE);
    }

    @DisplayName("Test for a flush hand")
    @Test
    public void testFlush()
    {
        Card[] handFlush = new Card[7];
        handFlush[0] = new Card(Card.Suit.Clubs, Card.Rank.TWO);
        handFlush[1] = new Card(Card.Suit.Clubs, Card.Rank.THREE);
        handFlush[2] = new Card(Card.Suit.Hearts, Card.Rank.FOUR);
        handFlush[3] = new Card(Card.Suit.Spades, Card.Rank.FIVE);
        handFlush[4] = new Card(Card.Suit.Clubs, Card.Rank.SIX);
        handFlush[5] = new Card(Card.Suit.Clubs, Card.Rank.JACK);
        handFlush[6] = new Card(Card.Suit.Clubs, Card.Rank.ACE);
        Hand handReturned = evaluator.checkHand(handFlush);
        System.out.println(handReturned.toString());
        assertTrue(handReturned == Hand.FLUSH);
    }

    @DisplayName("Test for a straight hand")
    @Test
    public void testStraight()
    {
        Card[] handStraight = new Card[7];
        handStraight[0] = new Card(Card.Suit.Clubs, Card.Rank.TWO);
        handStraight[1] = new Card(Card.Suit.Diamonds, Card.Rank.THREE);
        handStraight[2] = new Card(Card.Suit.Hearts, Card.Rank.FOUR);
        handStraight[3] = new Card(Card.Suit.Spades, Card.Rank.FIVE);
        handStraight[4] = new Card(Card.Suit.Clubs, Card.Rank.SIX);
        handStraight[5] = new Card(Card.Suit.Diamonds, Card.Rank.JACK);
        handStraight[6] = new Card(Card.Suit.Hearts, Card.Rank.ACE);
        Hand handReturned = evaluator.checkHand(handStraight);
        System.out.println(handReturned.toString());
        assertTrue(handReturned == Hand.STRAIGHT);
    }

    @DisplayName("Test for a three of a kind hand")
    @Test
    public void testThreeOfKind()
    {
        Card[] handThreeKind = new Card[7];
        handThreeKind[0] = new Card(Card.Suit.Clubs, Card.Rank.TWO);
        handThreeKind[1] = new Card(Card.Suit.Clubs, Card.Rank.THREE);
        handThreeKind[2] = new Card(Card.Suit.Hearts, Card.Rank.THREE);
        handThreeKind[3] = new Card(Card.Suit.Diamonds, Card.Rank.THREE);
        handThreeKind[4] = new Card(Card.Suit.Spades, Card.Rank.FOUR);
        handThreeKind[5] = new Card(Card.Suit.Diamonds, Card.Rank.FIVE);
        handThreeKind[6] = new Card(Card.Suit.Clubs, Card.Rank.ACE);
        Hand handReturned = evaluator.checkHand(handThreeKind);
        System.out.println(handReturned.toString());
        assertTrue(handReturned == Hand.THREE_KIND);
    }

    @DisplayName("Test for a two pair hand")
    @Test
    public void testTwoPair()
    {
        Card[] handTwoPair = new Card[7];
        handTwoPair[0] = new Card(Card.Suit.Clubs, Card.Rank.TWO);
        handTwoPair[1] = new Card(Card.Suit.Clubs, Card.Rank.THREE);
        handTwoPair[2] = new Card(Card.Suit.Hearts, Card.Rank.THREE);
        handTwoPair[3] = new Card(Card.Suit.Spades, Card.Rank.FOUR);
        handTwoPair[4] = new Card(Card.Suit.Clubs, Card.Rank.FOUR);
        handTwoPair[5] = new Card(Card.Suit.Clubs, Card.Rank.FIVE);
        handTwoPair[6] = new Card(Card.Suit.Diamonds, Card.Rank.TEN);
        Hand handReturned = evaluator.checkHand(handTwoPair);
        System.out.println(handReturned.toString());
        assertTrue(handReturned == Hand.TWO_PAIR);
    }

    @DisplayName("Test for a one pair hand")
    @Test
    public void testOnePair()
    {
        Card[] handOnePair = new Card[7];
        handOnePair[0] = new Card(Card.Suit.Clubs, Card.Rank.TWO);
        handOnePair[1] = new Card(Card.Suit.Clubs, Card.Rank.THREE);
        handOnePair[2] = new Card(Card.Suit.Hearts, Card.Rank.THREE);
        handOnePair[3] = new Card(Card.Suit.Clubs, Card.Rank.FOUR);
        handOnePair[4] = new Card(Card.Suit.Diamonds, Card.Rank.FIVE);
        handOnePair[5] = new Card(Card.Suit.Hearts, Card.Rank.JACK);
        handOnePair[6] = new Card(Card.Suit.Spades, Card.Rank.QUEEN);
        Hand handReturned = evaluator.checkHand(handOnePair);
        System.out.println(handReturned.toString());
        assertTrue(handReturned == Hand.PAIR);
    }

    //quick method to test if array of cards is in order
    private Boolean testOrder(Card[] sortedHand)
    {
        for(int i = 1; i < sortedHand.length; i++)
        {
            if(sortedHand[i-1].getCardRank().ordinal() > sortedHand[i].getCardRank().ordinal())
                return false;
        }
        return true;
    }
}
