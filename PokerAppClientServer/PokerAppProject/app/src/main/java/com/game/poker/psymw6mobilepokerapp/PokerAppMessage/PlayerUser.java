package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;

public class PlayerUser extends User {
    public Card[] hand;
    private Card highCard;
    private Card highCard2;
    private Card kicker;
    private Card[] fullHand;

    private int id;
    private boolean isDealer;
    private boolean isFolded;
    private boolean isActive;
    private boolean inGame;
    private int currentBet;
    private static final long serialVersionUID = 1452706986345L;

    public PlayerUser(String user_id, int currency, String username)
    {
        super(user_id, currency, username);
        hand = new Card[2];
        isFolded = false;
        isActive = true;
        inGame = true;
        isDealer = false;
    }

    public void setHand(Card[] hand)
    {
        this.hand = hand;
    }

    public Card[] getHand()
    {
        return hand;
    }

    public void setHighCard(Card highCard)
    {
        this.highCard = highCard;
    }

    public void setHighCard2(Card highCard2)
    {
        this.highCard2 = highCard2;
    }

    public void setKicker(Card kicker)
    {
        this.kicker = kicker;
    }

    public void setFullHand(Card[] fullHand)
    {
        this.fullHand = fullHand;
    }

    public void setID(int id)
    {
        this.id = id;
    }

    public void setDealer()
    {
        isDealer = !isDealer;
    }

    public int getCurrency()
    {
        return currency;
    }

    public Card getHighCard()
    {
        return highCard;
    }

    public Card getKicker()
    {
        return kicker;
    }

    public Card getHighCard2()
    {
        return highCard2;
    }

    public Card[] getFullHand()
    {
        return fullHand;
    }

    public boolean isFolded()
    {
        return isFolded;
    }

    public int getID()
    {
        return id;
    }

    public boolean getDealer()
    {
        return isDealer;
    }

    public void fold()
    {
        isFolded = true;
    }

    public void unFold()
    {
        isFolded = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public void toggleActive()
    {
        isActive = !isActive;
    }

    public boolean isInGame()
    {
        return inGame;
    }

    public void toggleInGame()
    {
        inGame = !inGame;
    }

    public boolean setCurrentBet(int bet)
    {
        if(bet > currency)
        {
            currentBet = currency;
            currency = 0;
            return false;
        }
        else
        {
            currency -= bet;
            currentBet += bet;
            return true;
        }
    }

    public int getCurrentBet()
    {
        return currentBet;
    }

    public void resetBet()
    {
        currentBet = 0;
    }

}
