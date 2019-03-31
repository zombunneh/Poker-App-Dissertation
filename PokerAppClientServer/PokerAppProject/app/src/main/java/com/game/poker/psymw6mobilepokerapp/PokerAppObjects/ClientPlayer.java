package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.Surface.GameViewSurface;

public class ClientPlayer extends GameObject {

    private Bitmap player;

    /**
     * Constructs a card bitmap from parameters
     *
     * @param image The complete bitmap to create the card image from
     * @param x x coord
     * @param y y coord
     * @param row Row of the image to take the card from
     * @param col Col of the image to take the card from
     */
    public ClientPlayer(Bitmap image, int x, int y, int row, int col)
    {
        super(image,x, y, 1, 1);

        player = this.createSubImageAt(row, col);
    }

    /**
     * Update method to change which player image is displayed
     *
     * @param row New row of the image to take from
     * @param col New col of the image to take from
     */
    public void update(int row, int col)
    {

        player = this.createSubImageAt(row, col);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(player, x, y, null);
    }

    /**
     * Getter for the image bitmap created from supplied parameters
     *
     * @return The image bitmap
     */
    public Bitmap getBitmap()
    {
        return player;
    }
}
