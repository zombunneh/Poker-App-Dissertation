package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;


public class GameUser extends User {
    public String lastLogin;
    public int loginStreak;
    public boolean loginStreakChanged;
    public int hands_played;
    public int hands_won;
    public int win_rate;
    public int max_winnings;
    public int max_chips;
    private static final long serialVersionUID = 1587469852L;

    /**
     * Object used for returning database details in queries
     * Holds information from the database only
     *
     * @param lastLogin The last login timestamp of the user
     * @param user_id The id of the user
     * @param currency The amount of currency the user has
     * @param loginStreak The current amount of consecutive days the player has logged in
     * @param username The username of the user
     * @param hands_played The amount of hands played
     * @param hands_won The amount of hands won
     * @param win_rate The win rate of the user
     * @param max_winnings The maximum amount of currency the player has held at one time
     * @param max_chips The maximum amount of currency the player has won in a single hand
     */
    public GameUser (String lastLogin,
                     String user_id,
                     int currency,
                     int loginStreak,
                     String username,
                     int hands_played,
                     int hands_won,
                     int win_rate,
                     int max_winnings,
                     int max_chips)
    {
        super(user_id, currency, username);
        this.lastLogin = lastLogin;
        this.hands_played = hands_played;
        this.hands_won = hands_won;
        this.win_rate = win_rate;
        this.max_winnings = max_winnings;
        this.max_chips = max_chips;
        this.loginStreak = loginStreak;
        this.loginStreakChanged = false;
    }
}
