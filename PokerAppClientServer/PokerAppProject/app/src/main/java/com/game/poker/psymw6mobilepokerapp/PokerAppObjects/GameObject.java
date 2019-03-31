package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import android.graphics.Bitmap;

public class GameObject {

    protected Bitmap image;

    protected final int rowCount;
    protected final int colCount;

    protected final int WIDTH;
    protected final int HEIGHT;

    protected final int width;
    protected final int height;
    protected int x;
    protected int y;

    /**
     * Super class for all game objects to be drawn
     *
     * @param image
     * @param x
     * @param y
     * @param rowCount
     * @param colCount
     */
    public GameObject(Bitmap image, int x, int y, int rowCount, int colCount)
    {
        this.image = image;
        this.x = x;
        this.y = y;
        this.rowCount = rowCount;
        this.colCount = colCount;

        this.WIDTH = image.getWidth();
        this.HEIGHT = image.getHeight();

        this.width = WIDTH / colCount;
        this.height = HEIGHT / rowCount;
    }

    /**
     * Creates a new bitmap image from the image using a row and col
     *
     * @param row The row of the image to draw from
     * @param col The col of the image to draw from
     * @return A bitmap created from the supplied parameters
     */
    protected Bitmap createSubImageAt(int row, int col)
    {
        Bitmap subImage = Bitmap.createBitmap(image, col * width, row * height, width, height);
        return subImage;
    }

    /**
     * Getter for x member variable
     *
     * @return int value of x
     */
    public int getX() {
        return x;
    }

    /**
     * Getter for y member variable
     *
     * @return int value of y
     */
    public int getY() {
        return y;
    }

    /**
     * Getter for height member variable
     *
     * @return int value of height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Getter for width member variable
     *
     * @return int value of width
     */
    public int getWidth() {
        return width;
    }
}
