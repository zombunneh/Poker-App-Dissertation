package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandQueue;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.ClientCard;
import com.game.poker.psymw6mobilepokerapp.PokerAppRunnable.GameListener;
import com.game.poker.psymw6mobilepokerapp.PokerAppService.ServerConnectionService;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.Surface.GameViewSurface;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments.Bet_Slider;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments.Call_Button;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments.Check_Button;
import com.game.poker.psymw6mobilepokerapp.R;

import java.net.Socket;

public class GameView extends AppCompatActivity {

    private ServerConnectionService.ServerBinder serviceBinder;
    private ServerConnectionService serviceInstance;
    private GameViewModel model;
    private GameViewActions actions;
    private GameViewUpdater updater;
    private GameViewSurface surface;

    private Call_Button call_button_frag;
    private Check_Button check_button_frag;
    public Bet_Slider bet_slider_frag;

    private ImageView communityCard1;
    private ImageView communityCard2;
    private ImageView communityCard3;
    private ImageView communityCard4;
    private ImageView communityCard5;

    private ImageView handCard1;
    private ImageView handCard2;

    private boolean stopThreads = false;

    public static final String TAG = "gameView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        call_button_frag = Call_Button.newInstance();
        check_button_frag = Check_Button.newInstance();
        bet_slider_frag = Bet_Slider.newInstance();

        setContentView(R.layout.game_view_activity);

        communityCard1 = findViewById(R.id.communityCard1);
        communityCard2 = findViewById(R.id.communityCard2);
        communityCard3 = findViewById(R.id.communityCard3);
        communityCard4 = findViewById(R.id.communityCard4);
        communityCard5 = findViewById(R.id.communityCard5);

        handCard1 = findViewById(R.id.handCard1);
        handCard2 = findViewById(R.id.handCard2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ServerConnectionService.class);
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        Thread monitor = new Thread(new Runnable() {
            @Override
            public void run() {
                while(serviceInstance == null)
                {
                    try
                    {
                        Thread.sleep(500);
                    }
                    catch(InterruptedException e)
                    {
                        Log.d(TAG, e.toString());
                    }
                }

                if(serviceInstance.isServerConnected()) {
                    surface = findViewById(R.id.gameViewSurface);
                    updater = new GameViewUpdater(surface);
                    if(surface == null)
                    {
                        Log.d(TAG, "surface null");
                    }

                    CommandQueue queue = new CommandQueue();
                    CommandInvoker invoker = new CommandInvoker(serviceInstance.getClientSocket(), serviceInstance.getOut(), queue, GameView.this);
                    invoker.startInvoker(true);

                    GameListener listener = new GameListener(serviceInstance.getClientSocket(), serviceInstance.getIn(), queue);
                    listener.setRunning(true);

                    Thread invokerThread = new Thread(invoker, "invokerThread");
                    Thread listenerThread = new Thread(listener, "listenerThread");

                    invokerThread.start();
                    listenerThread.start();

                    model = invoker.getModel();
                    actions = new GameViewActions(model);


                    while (invoker.isInvoked() && listener.isRunning()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {

                        }
                        if(stopThreads == true)
                        {
                            invoker.startInvoker(false);
                            listener.setRunning(false);
                        }
                    }
                }
            }
        });
        monitor.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(connection!= null)
        {
            unbindService(connection);
            connection = null;
        }
        if(serviceInstance != null)
        {
            serviceInstance = null;
        }
        stopThreads = true;
        Log.d(TAG, "activity destroyed");
    }

    public void setCardImageViews()
    {
        int[][] cards = new int[2][2];

        for(int i = 0; i < 2; i++)
        {
            cards[i][0] = model.myPlayer.getMyHand()[i].getCardSuit().ordinal();
            cards[i][1] = model.myPlayer.getMyHand()[i].getCardRank().ordinal();
        }

        Bitmap handCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards, 200, 300);
        ClientCard handCardBitmap = new ClientCard(handCardBitmap1, 100, 500, cards[0][0], cards[0][1]);

        handCard1.setImageBitmap(handCardBitmap.getBitmap());
        handCardBitmap.update(cards[1][0], cards[1][1]);
        handCard2.setImageBitmap(handCardBitmap.getBitmap());

    }

    public void setCommunityImageViews()
    {
        if(model.getCommunityCards()[0] != null)
        {
            Log.d(TAG, "add comm");
            int[][] cards = new int[3][2];

            for(int i = 0; i < 3; i++) {
                cards[i][0] = model.getCommunityCards()[i].getCardSuit().ordinal();
                cards[i][1] = model.getCommunityCards()[i].getCardRank().ordinal();
            }

            Bitmap communityCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards, 194, 288);
            ClientCard communityCardBitmap = new ClientCard(communityCardBitmap1, 100, 500, cards[0][0], cards[0][1]);

            communityCard1.setImageBitmap(communityCardBitmap.getBitmap());
            communityCardBitmap.update(cards[1][0], cards[1][1]);
            communityCard2.setImageBitmap(communityCardBitmap.getBitmap());
            communityCardBitmap.update(cards[2][0], cards[2][1]);
            communityCard3.setImageBitmap(communityCardBitmap.getBitmap());
        }
    }

    public void setCommunityImageView()
    {
        if(communityCard4.getDrawable() == null && model.getCommunityCards()[3] != null)
        {
            Log.d(TAG, "set tr");
            Bitmap communityCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards, 194, 288);
            ClientCard communityCardBitmap = new ClientCard(communityCardBitmap1, 100, 500, model.getCommunityCards()[3].getCardSuit().ordinal(),model.getCommunityCards()[3].getCardRank().ordinal());

            communityCard4.setImageBitmap(communityCardBitmap.getBitmap());
        }
        if(communityCard5.getDrawable() == null && model.getCommunityCards()[4] != null)
        {
            Log.d(TAG, "set tr");
            Bitmap communityCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards, 194, 288);
            ClientCard communityCardBitmap = new ClientCard(communityCardBitmap1, 100, 500, model.getCommunityCards()[4].getCardSuit().ordinal(),model.getCommunityCards()[4].getCardRank().ordinal());

            communityCard5.setImageBitmap(communityCardBitmap.getBitmap());
        }
    }

    public void removeCommunityCards()
    {
        Log.d(TAG, "remove comm");
        communityCard1.setImageDrawable(null);
        communityCard2.setImageDrawable(null);
        communityCard3.setImageDrawable(null);
        communityCard4.setImageDrawable(null);
        communityCard5.setImageDrawable(null);
    }

    public void removeHand()
    {
        handCard1.setImageDrawable(null);
        handCard2.setImageDrawable(null);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
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
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeResource(res, resID, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resID, options);
    }

    public void addCallFrag()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.callcheckbutton, call_button_frag).commitNow();
    }

    public void addCheckFrag()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.callcheckbutton, check_button_frag).commitNow();
    }

    public void addSliderFrag()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.betSliderLayout, bet_slider_frag).commitNow();
    }

    public void hideSliderFrag()
    {
        getSupportFragmentManager().beginTransaction().hide(bet_slider_frag).commitNow();
    }

    public GameViewActions getActions()
    {
        return actions;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "service connected");
            serviceBinder = (ServerConnectionService.ServerBinder) service;
            serviceInstance = serviceBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "service disconnected");
            serviceBinder = null;
        }
    };


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public GameViewUpdater getUpdater() {
        return updater;
    }
}
