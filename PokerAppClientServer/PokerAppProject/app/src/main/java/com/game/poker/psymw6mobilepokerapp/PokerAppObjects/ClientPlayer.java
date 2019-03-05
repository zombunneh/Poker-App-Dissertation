package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.Surface.GameViewSurface;

public class ClientPlayer extends GameObject {

    private Bitmap player;

    public ClientPlayer(Bitmap image, int x, int y, int row, int col)
    {
        super(image,x, y, 1, 1);

        player = this.createSubImageAt(row, col);
    }

    public void update(int row, int col)
    {
        //update timer?
        player = this.createSubImageAt(row, col);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(player, x, y, null);
    }

    public Bitmap getBitmap()
    {
        return player;
    }
}
