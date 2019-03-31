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

    /**
     * Obsolete constructor from using a game surface to display images
     *
     * @param surface
     * @param image
     * @param x
     * @param y
     * @param row
     * @param col
     */
    public ClientCard(GameViewSurface surface, Bitmap image, int x, int y, int row, int col)
    {
        super(image, x, y, 4, 13);

        this.gameSurface = surface;

        card = this.createSubImageAt(row, col);
    }

    /**
     * Constructs a card bitmap from parameters
     *
     * @param image The complete bitmap to create the card image from
     * @param x x coord
     * @param y y coord
     * @param row Row of the image to take the card from
     * @param col Col of the image to take the card from
     */
    public ClientCard(Bitmap image, int x, int y, int row, int col)
    {
        super(image, x, y, 4, 13);

        card = this.createSubImageAt(row, col);
    }

    /**
     * Update method to change which card is displayed from the image
     *
     * @param row New row of the image to take the card from
     * @param col New col of the image to take the card from
     */
    public void update(int row, int col)
    {
        card = this.createSubImageAt(row, col);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(card, x, y, null);
    }

    /**
     * Getter for the image bitmap created from supplied parameters
     *
     * @return The image bitmap
     */
    public Bitmap getBitmap()
    {
        return card;
    }
}
