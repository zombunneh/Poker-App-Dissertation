package com.game.poker.psymw6mobilepokerapp.PokerAppMessage;


// includes extra information not relevant to a user connected to a com.game.poker table
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
