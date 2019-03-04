package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.content.Context;

public class GameViewController {
    private GameViewModel model;
    private Context view;

    public GameViewController(GameViewModel model, Context viewContext)
    {
        this.model = model;
        this.view = viewContext;
    }

    public void updateView()
    {

    }
}
