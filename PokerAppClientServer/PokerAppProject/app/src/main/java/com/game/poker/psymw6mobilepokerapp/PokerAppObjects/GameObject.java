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

    protected Bitmap createSubImageAt(int row, int col)
    {
        Bitmap subImage = Bitmap.createBitmap(image, col * width, row * height, width, height);
        return subImage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
