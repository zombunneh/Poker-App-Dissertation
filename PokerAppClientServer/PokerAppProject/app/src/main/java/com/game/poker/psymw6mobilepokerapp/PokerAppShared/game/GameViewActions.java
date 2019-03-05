package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUserMove;

public class GameViewActions {

    private GameViewModel model;

    public static final String TAG = "gvActions";

    public GameViewActions(GameViewModel model)
    {
        this.model = model;
    }

    public void pressedFold()
    {
        Log.d(TAG, "fold");
        model.pressedButton(PlayerUserMove.FOLD, 0);
    }

    public void pressedCall()
    {
        Log.d(TAG, "call");
        model.pressedButton(PlayerUserMove.CALL, 0);
    }

    public void pressedCheck()
    {
        Log.d(TAG, "check");
        model.pressedButton(PlayerUserMove.CHECK, 0);
    }


    public void pressedRaise(int bet)
    {
        Log.d(TAG, "bet/raise");
        model.pressedButton(PlayerUserMove.RAISE, bet);
    }
}
