package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import android.graphics.Bitmap;

import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameViewSurface;

public class ClientPlayer extends GameObject {
    private GameViewSurface gameSurface;

    public ClientPlayer(GameViewSurface surface, Bitmap image, int x, int y)
    {
        super(image,x, y, 1, 1);
    }
}
