package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;

public class GameViewThread extends Thread{

    private boolean running;
    private GameViewSurface surface;
    private SurfaceHolder holder;

    public GameViewThread(GameViewSurface surface, SurfaceHolder holder)
    {
        this.surface = surface;
        this.holder = holder;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();

        while(running)
        {
            Canvas canvas = null;

            try
            {
                canvas = this.holder.lockCanvas();

                synchronized(canvas)
                {
                    this.surface.update();
                    this.surface.draw(canvas);
                }
            }
            catch(Exception e)
            {

            }
            finally
            {
                if(canvas != null)
                {
                    this.holder.unlockCanvasAndPost(canvas);
                }
            }
            long now = System.nanoTime();
            long waitTime = (now - startTime) / 1000000;
            if(waitTime < 10)
            {
                waitTime = 10;
            }

            try
            {
                this.sleep(waitTime);
            }
            catch(InterruptedException e)
            {

            }
            startTime = System.nanoTime();
        }
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }
}
