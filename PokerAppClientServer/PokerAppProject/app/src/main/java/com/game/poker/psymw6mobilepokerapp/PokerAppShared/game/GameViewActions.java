package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUserMove;

public class GameViewActions {

    private GameViewModel model;

    public static final String TAG = "gvActions";

    /**
     * Constructor for the actions object which manages button presses
     *
     * @param model
     */
    public GameViewActions(GameViewModel model)
    {
        this.model = model;
    }

    /**
     * Notifies the model that the fold button was pressed
     */
    public void pressedFold()
    {
        model.pressedButton(PlayerUserMove.FOLD, 0);
    }

    /**
     * Notifies the model that the call button was pressed
     */
    public void pressedCall()
    {
        model.pressedButton(PlayerUserMove.CALL, 0);
    }

    /**
     * Notifies the model that the check button was pressed
     */
    public void pressedCheck()
    {
        model.pressedButton(PlayerUserMove.CHECK, 0);
    }

    /**
     * Notifies the model that the raise or bet button was pressed
     */
    public void pressedRaise(int bet)
    {
        model.pressedButton(PlayerUserMove.RAISE, bet);
    }
}
