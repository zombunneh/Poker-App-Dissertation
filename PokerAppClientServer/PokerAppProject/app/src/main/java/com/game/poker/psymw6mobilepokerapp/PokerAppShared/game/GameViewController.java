package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.content.Context;
import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.Surface.GameViewSurface;

import java.util.Observable;
import java.util.Observer;

public class GameViewController implements Observer {
    private GameViewModel model;
    private Context view;
    private GameView gameView;
    private GameViewUpdater updater;

    public static final String TAG = "controller :3";

    public GameViewController(GameViewModel model, Context viewContext)
    {
        this.model = model;
        this.view = viewContext;
        gameView = (GameView) view;
        updater = gameView.getUpdater();
    }

    public void updateView(GameViewModel model, final Object arg)
    {
        Log.d(TAG, "update view");
        if(arg instanceof Card[])
        {
            gameView.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameView.setCommunityImageViews();
                }
            });
        }
        else if(arg instanceof Card)
        {
            Log.d(TAG, "turn/river update");
            gameView.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameView.setCommunityImageView();
                }
            });
        }
        else if(arg instanceof GameViewModel.State)
        {
            if(arg == GameViewModel.State.CALL)
            {
                Log.d(TAG, "state = call");
                gameView.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameView.addCallFrag();
                    }
                });
            }
            if(arg == GameViewModel.State.CHECK)
            {
                Log.d(TAG, "state = check");
                gameView.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameView.addCheckFrag();
                    }
                });
            }
        }
    }

    public void updateView(GameViewModel.MyPlayer player, final Object arg)
    {
        if(arg instanceof Card[])
        {
            gameView.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameView.setCardImageViews();
                }
            });
        }
    }

    public void updateView(GameViewModel.Bet bet, final Object arg)
    {

    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d(TAG, "change observed");
        if(o instanceof GameViewModel)
        {
            this.updateView((GameViewModel) o, arg);
        }
        else if(o instanceof GameViewModel.Bet)
        {
            this.updateView((GameViewModel.Bet) o, arg);
        }
        else if(o instanceof GameViewModel.MyPlayer)
        {
            this.updateView((GameViewModel.MyPlayer) o, arg);
        }
    }
}
