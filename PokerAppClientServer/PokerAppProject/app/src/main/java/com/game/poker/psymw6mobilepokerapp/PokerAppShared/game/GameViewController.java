package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.content.Context;
import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;

import java.util.Observable;
import java.util.Observer;

public class GameViewController implements Observer {
    private GameViewModel model;
    private Context view;
    private GameView gameView;

    public static final String TAG = "controller :3";

    public GameViewController(GameViewModel model, Context viewContext)
    {
        this.model = model;
        this.view = viewContext;
        gameView = (GameView) view;
    }

    public void updateView(GameViewModel model, Object arg)
    {
        Log.d(TAG, "update view");
        if(arg instanceof Card[])
        {

        }
        else if(arg instanceof GameViewModel.State)
        {
            if(arg == GameViewModel.State.CALL)
            {
                Log.d(TAG, "state= call");
                gameView.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameView.addCallFrag();
                    }
                });
            }
            if(arg == GameViewModel.State.CHECK)
            {
                Log.d(TAG, "state= check");
                gameView.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameView.addCheckFrag();
                    }
                });
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d(TAG, "change observed");
        if(o instanceof GameViewModel)
        {
            this.updateView((GameViewModel) o, arg);
        }
    }
}
