package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.ClientCard;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.ClientPlayer;
import com.game.poker.psymw6mobilepokerapp.R;

public class GameViewSurface extends SurfaceView implements SurfaceHolder.Callback {

    private GameViewThread gameThread;
    private ClientCard handCard1;
    private ClientCard handCard2;
    private ClientCard flop1;
    private ClientCard flop2;
    private ClientCard flop3;
    private ClientCard river;
    private ClientCard turn;
    private ClientPlayer myPlayer;

    private GameViewModel.State state;

    public GameViewSurface(Context context)
    {
        super(context);

        this.setFocusable(true);

        this.getHolder().addCallback(this);

        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    public GameViewSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setFocusable(true);

        this.getHolder().addCallback(this);

        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    public GameViewSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setFocusable(true);

        this.getHolder().addCallback(this);

        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    public void update()
    {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.handCard1.draw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Bitmap handCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards, 400, 400);
        this.handCard1 = new ClientCard(this, handCardBitmap1, 100, 500, 0, 1);

        this.gameThread = new GameViewThread(this, holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry)
        {
            try
            {
                this.gameThread.setRunning(false);

                this.gameThread.join();
            }
            catch(InterruptedException e)
            {

            }
            retry = true;
        }
    }

    public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth)
        {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth)
            {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resID, int reqWidth, int reqHeight)
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resID, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resID, options);
    }
}
