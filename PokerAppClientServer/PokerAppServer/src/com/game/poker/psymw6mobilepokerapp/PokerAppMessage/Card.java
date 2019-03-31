package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

import java.io.Serializable;

public class Card implements Serializable {

    public static final long serialVersionUID = 12923034298L;

    public enum Suit
    {
        Spades,
        Hearts,
        Diamonds,
        Clubs
    }

    public enum Rank
    {
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING,
        ACE
    }

    private Suit cardSuit;
    private Rank cardRank;

    /**
     * Card consists of 2 attributes, a suit and a rank which can be checked via the getter methods.
     *
     * @param suit The suit of the card
     * @param rank The rank of the card
     */
    public Card(Suit suit, Rank rank)
    {
        this.cardSuit = suit;
        this.cardRank = rank;
    }

    /**
     *
     * @return The card's suit
     */
    public Suit getCardSuit()
    {
        return cardSuit;
    }

    /**
     *
     * @return The card's rank
     */
    public Rank getCardRank()
    {
        return cardRank;
    }
}
