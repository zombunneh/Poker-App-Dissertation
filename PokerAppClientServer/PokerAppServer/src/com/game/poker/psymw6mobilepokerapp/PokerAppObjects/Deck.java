package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;

import java.util.Random;

public class Deck {
    private Card[] deckOfCards = new Card[52];
    private int topCard;

    /*
    Deck consists of Cards, function to shuffle via Durstenfeld algorithm
    function to draw a card from the top of the deck for purpose of distributing to players/community cards
     */
    public Deck()
    {
        for(int i = 0; i < 52; i++)
        {
            deckOfCards[i] = new Card(Card.Suit.values()[i % 4], Card.Rank.values()[i % 13]);
        }
        topCard = 0;
    }

    public void shuffleDeck()
    {
        int index = 0;
        int length = 52;
        while(index < length)
        {
            int rand_index = selectRandIndex(index, length);
            swap(deckOfCards, index, rand_index);
            index += 1;
        }
    }

    public int selectRandIndex(int index, int length)
    {
        int toEnd = length - index;
        Random rand = new Random();
        return index + rand.nextInt(toEnd);
    }

    public void swap(Card[] array, int index, int randIndex)
    {
        Card temp = array[index];
        array[index] = array[randIndex];
        array[randIndex] = temp;
    }

    public Card drawCard()
    {
        return deckOfCards[topCard++];
    }
}
