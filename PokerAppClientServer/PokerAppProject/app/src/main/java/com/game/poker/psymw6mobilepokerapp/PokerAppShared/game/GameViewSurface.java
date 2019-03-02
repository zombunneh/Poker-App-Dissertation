package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.ClientCard;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.ClientPlayer;

public class GameViewSurface extends SurfaceView implements SurfaceHolder.Callback {

    private GameViewThread gameThread;
    private ClientCard handCard1;
    private ClientCard flop1;
    private ClientCard flop2;
    private ClientCard flop3;
    private ClientCard river;
    private ClientCard turn;
    private ClientPlayer myPlayer;

    public GameViewSurface(Context context)
    {
        super(context);

        this.setFocusable(true);

        this.getHolder().addCallback(this);
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
