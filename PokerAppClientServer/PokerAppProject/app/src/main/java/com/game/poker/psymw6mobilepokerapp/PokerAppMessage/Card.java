package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

public class Card {
    public enum Suit
    {
        Hearts,
        Diamonds,
        Clubs,
        Spades
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

    /*
    Card consists of 2 attributes, a suit and a rank which can be checked via the getter methods.
    Might need to implement extra methods for additional functionality later
     */
    public Card(Suit suit, Rank rank)
    {
        this.cardSuit = suit;
        this.cardRank = rank;
    }

    public Suit getCardSuit()
    {
        return cardSuit;
    }

    public Rank getCardRank()
    {
        return cardRank;
    }
}
