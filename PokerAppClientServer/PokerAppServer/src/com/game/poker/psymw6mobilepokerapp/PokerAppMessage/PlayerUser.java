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
    private boolean isReady;
    private int currentBet;
    private int lastBet;
    private int inactiveTurns;

    public int hands_played;
    public int hands_won;
    public int win_rate;
    public int max_winnings;
    public int max_chips;

    private static final long serialVersionUID = 1452706986345L;

    /**
     * A subclass of the user object which contains additional methods for in game variables and statistics
     *
     * @param user_id The id of the user
     * @param currency The amount of currency the user has
     * @param username The username of the user
     */
    public PlayerUser(String user_id, int currency, String username)
    {
        super(user_id, currency, username);
        hand = new Card[2];
        isFolded = false;
        isActive = false;
        inGame = true;
        isDealer = false;
        isReady = false;
        inactiveTurns = 0;
    }

    /**
     * A subclass of the user object which contains additional methods for in game variables and statistics
     *
     * @param user The SocketUser to construct a PlayerUser from
     */
    public PlayerUser(SocketUser user)
    {
        super(user.user_id, user.currency, user.username);
        this.hands_played = user.hands_played;
        this.hands_won = user.hands_won;
        this.win_rate = user.win_rate;
        this.max_winnings = user.max_winnings;
        this.max_chips = user.max_chips;

        hand = new Card[2];
        isFolded = false;
        isActive = false;
        inGame = true;
        isDealer = false;
        isReady = false;
        inactiveTurns = 0;
    }

    /**
     * Sets the player's hand to the array passed in
     *
     * @param hand Array of cards
     */
    public void setHand(Card[] hand)
    {
        this.hand = hand;
    }

    /**
     *
     * @return The player's hand array
     */
    public Card[] getHand()
    {
        return hand;
    }

    /**
     * Sets the player's high card for use in hand evaluation
     *
     * @param highCard The high card to set
     */
    public void setHighCard(Card highCard)
    {
        this.highCard = highCard;
    }

    /**
     * Sets the player's second high card for use in hand evaluation
     *
     * @param highCard2 The high card to set
     */
    public void setHighCard2(Card highCard2)
    {
        this.highCard2 = highCard2;
    }

    /**
     * Sets the player's kicker for use in hand evaluation
     *
     * @param kicker The kicker to set
     */
    public void setKicker(Card kicker)
    {
        this.kicker = kicker;
    }

    /**
     * Sets the player's full hand for use in hand evaluation
     * The full hand is a combination of the players hand card and community cards to make the strongest possible 5 card hand
     *
     * @param fullHand An array of 5 cards to set as the player's full hand
     */
    public void setFullHand(Card[] fullHand)
    {
        this.fullHand = fullHand;
    }

    /**
     * Set the player's id which will be used to send commands and retrieve the user when necessary from lists
     *
     * @param id The id to set
     */
    public void setID(int id)
    {
        this.id = id;
    }

    /**
     * Toggles the player state of dealer
     */
    public void setDealer()
    {
     isDealer = !isDealer;
    }

    /**
     *
     * @return The player's amount of currency
     */
    public int getCurrency()
    {
        return currency;
    }

    /**
     *
     * @return The player's current high card
     */
    public Card getHighCard()
    {
        return highCard;
    }

    /**
     *
     * @return The player's current kicker
     */
    public Card getKicker()
    {
        return kicker;
    }

    /**
     *
     * @return The player's current second high card
     */
    public Card getHighCard2()
    {
        return highCard2;
    }

    /**
     *
     * @return The player's full hand i.e their strongest 5 card combination of hand cards and community cards
     */
    public Card[] getFullHand()
    {
        return fullHand;
    }

    /**
     *
     * @return True if the user has folded, false if not
     */
    public boolean isFolded()
    {
        return isFolded;
    }

    /**
     *
     * @return The player's current ID
     */
    public int getID()
    {
        return id;
    }

    /**
     *
     * @return True if the player is the dealer, false if not
     */
    public boolean getDealer()
    {
        return isDealer;
    }

    /**
     * Set the player to be folded
     */
    public void fold()
    {
        isFolded = true;
    }

    /**
     * Set the player to not be folded
     */
    public void unFold()
    {
        isFolded = false;
    }

    /**
     *
     * @return true if the player is active, false it not
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Toggles whether the player is active or inactive
     */
    public void toggleActive()
    {
        isActive = !isActive;
    }

    /**
     *
     * @return True if the player is in game, false if not
     */
    public boolean isInGame()
    {
        return inGame;
    }

    /**
     * Toggles whether the player is in game or not
     */
    public void toggleInGame()
    {
        inGame = !inGame;
    }

    /**
     * Sets the players bet variables, if bet is larger than the amount of currency the player has the bet is automatically set to
     * remaining currency amount
     *
     * @param bet The amount to bet
     * @return
     */
    public boolean setCurrentBet(int bet)
    {
        if(bet > currency)
        {
            currentBet = currency;
            lastBet = currency;
            currency = 0;
            return false;
        }
        else
        {
            currency -= bet;
            currentBet += bet;
            lastBet = bet;
            return true;
        }
    }

    /**
     *
     * @return The player's current bet amount
     */
    public int getCurrentBet()
    {
        return currentBet;
    }

    /**
     *
     * @return The player's last bet amount
     */
    public int getLastBet()
    {
        return lastBet;
    }

    /**
     * Resets the player's current bet
     */
    public void resetBet()
    {
        currentBet = 0;
    }

    /**
     * Resets the player's last bet
     */
    public void resetLastBet()
    {
        lastBet = 0;
    }

    /**
     * Increments how many turns the player has been inactive
     */
    public void incrementInactive()
    {
        inactiveTurns++;
    }

    /**
     *
     * @return The amount of turns the player has been inactive in the current game
     */
    public int getInactivity()
    {
        return inactiveTurns;
    }

    /**
     * Toggles whether the player is ready or not
     */
    public void toggleReady()
    {
        isReady = !isReady;
    }

    /**
     * Increases the player's amount of currency held by a specified amount
     *
     * @param pot The amount of currency to give the player
     */
    public void giveCurrency(int pot)
    {
        currency += pot;
        System.out.println("given " + pot );
    }

    /**
     * Increments the player's amount of hands won, used for storing updated stats in database
     */
    public void incrementWins()
    {
        hands_won++;
    }

    /**
     * Sets the player's maximum amount of currency held at one time, used for storing updated stats in database
     *
     * @param newMax The new maximum amount of currency
     */
    public void newMaxChips(int newMax)
    {
        max_chips = newMax;
    }

    /**
     * Sets the player's maximum amount of currency won in a single hand, used for storing updated stats in database
     *
     * @param newMax The new maximum amount of currency won in a hand
     */
    public void newMaxWinnings(int newMax)
    {
        max_winnings = newMax;
    }

    /**
     * Calculated a win rate percentage based on hands won vs hand played, used for storing updated stats in database
     */
    public void adjustWinRate()
    {
        double rate = ((double)hands_won / hands_played);
        win_rate = (int) Math.round(rate * 100);
    }

    /**
     * Increments the player's amount of hands played, used for storing updated stats in database
     */
    public void incrementHandsPlayed()
    {
        hands_played++;
    }
}
