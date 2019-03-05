package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.Surface.GameViewSurface;

public class ClientCard extends GameObject {

    private GameViewSurface gameSurface;

    public static final int ROW_SPADES = 0;
    public static final int ROW_HEARTS = 1;
    public static final int ROW_DIAMONDS = 2;
    public static final int ROW_CLUBS = 3;

    private Bitmap card;
    private long lastDrawNanoTime = -1;

    public ClientCard(GameViewSurface surface, Bitmap image, int x, int y, int row, int col)
    {
        super(image, x, y, 4, 13);

        this.gameSurface = surface;

        card = this.createSubImageAt(row, col);
    }

    public ClientCard(Bitmap image, int x, int y, int row, int col)
    {
        super(image, x, y, 4, 13);

        card = this.createSubImageAt(row, col);
    }

    //function to zoom on tap
    public void update(int row, int col)
    {
        long now = System.nanoTime();

        if(lastDrawNanoTime == -1)
        {
            lastDrawNanoTime = now;
        }

        card = this.createSubImageAt(row, col);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(card, x, y, null);
        this.lastDrawNanoTime = System.nanoTime();
    }

    public Bitmap getBitmap()
    {
        return card;
    }
}
