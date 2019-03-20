package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandQueue;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerMove;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUserMove;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.ClientCard;
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.ClientPlayer;
import com.game.poker.psymw6mobilepokerapp.PokerAppRunnable.GameListener;
import com.game.poker.psymw6mobilepokerapp.PokerAppService.ServerConnectionService;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments.Bet_Slider;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments.Call_Button;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments.Check_Button;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments.Zoomed_Cards;
import com.game.poker.psymw6mobilepokerapp.R;

import java.util.List;

public class GameView extends AppCompatActivity {
    //TODO disable buttons when not expecting input >:3
    private ServerConnectionService.ServerBinder serviceBinder;
    private ServerConnectionService serviceInstance;
    public GameViewModel model;
    private GameViewActions actions;

    private Call_Button call_button_frag;
    private Check_Button check_button_frag;
    public Bet_Slider bet_slider_frag;

    private ImageView[] communityCardViews;
    private ImageView[] handCards;
    private ImageView[] playerDisplays;
    private ImageView[] playerDisplayOverlays;

    private Button leaveButton;
    private TextView turnBroadcast;
    private TextView potDisplay;
    private TextView turnTime;
    private TextView betSliderAmount;

    private int[] playerDisplayIDs;

    private boolean stopThreads = false;
    private boolean myTurn = false;
    private int turnTimer = 0;
    private boolean sliderVisible = false;

    private Handler handler;

    public static final String TAG = "gameView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        call_button_frag = Call_Button.newInstance();
        check_button_frag = Check_Button.newInstance();
        bet_slider_frag = Bet_Slider.newInstance();

        onWindowFocusChanged(true);

        setContentView(R.layout.game_view_activity);

        communityCardViews = new ImageView[5];
        handCards = new ImageView[2];
        playerDisplays = new ImageView[5];
        playerDisplayOverlays = new ImageView[5];

        communityCardViews[0] = findViewById(R.id.communityCard1);
        communityCardViews[1] = findViewById(R.id.communityCard2);
        communityCardViews[2] = findViewById(R.id.communityCard3);
        communityCardViews[3] = findViewById(R.id.communityCard4);
        communityCardViews[4] = findViewById(R.id.communityCard5);

        for(ImageView view : communityCardViews)
        {
            view.setOnClickListener(listener);
        }

        handCards[0] = findViewById(R.id.handCard1);
        handCards[1] = findViewById(R.id.handCard2);

        for(ImageView view : handCards)
        {
            view.setOnClickListener(listener);
        }

        playerDisplays[0] = findViewById(R.id.player1);
        playerDisplays[1] = findViewById(R.id.player2);
        playerDisplays[2] = findViewById(R.id.player3);
        playerDisplays[3] = findViewById(R.id.player4);
        playerDisplays[4] = findViewById(R.id.player5);

        playerDisplayOverlays[0] = findViewById(R.id.player1overlay);
        playerDisplayOverlays[1] = findViewById(R.id.player2overlay);
        playerDisplayOverlays[2] = findViewById(R.id.player3overlay);
        playerDisplayOverlays[3] = findViewById(R.id.player4overlay);
        playerDisplayOverlays[4] = findViewById(R.id.player5overlay);

        leaveButton = findViewById(R.id.leaveButton);
        leaveButton.setOnClickListener(listener);

        turnBroadcast = findViewById(R.id.turnBroadcast);

        potDisplay = findViewById(R.id.potDisplay);

        turnTime = findViewById(R.id.timeRemaining);

        betSliderAmount = findViewById(R.id.betSliderAmount);

        playerDisplayIDs = new int[5];

        handler = new Handler();
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

                    Thread turnTimerThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(!stopThreads)
                            {
                                final String timeRemaining = String.format(getString(R.string.turnTimeRemaining), turnTimer);
                                if(myTurn && turnTimer >= 0)
                                {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            turnTime.setText(timeRemaining);
                                        }
                                    });

                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {

                                    }
                                    turnTimer--;
                                }
                                else
                                {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            turnTime.setText(getString(R.string.notYourTurn));
                                        }
                                    });
                                }
                            }

                        }
                    });

                    turnTimerThread.start();

                    while (invoker.isInvoked() && listener.isRunning()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {

                        }
                        if(stopThreads)
                        {
                            invoker.startInvoker(false);
                            listener.setRunning(false);

                            Log.d(TAG, "ended threads");
                        }
                    }
                    Log.d(TAG, "ended monitor thread");
                }
            }
        });
        monitor.start();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        stopThreads = true;
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
        ClientCard handCardBitmap = new ClientCard(handCardBitmap1, 0, 0, cards[0][0], cards[0][1]);

        handCards[0].setImageBitmap(handCardBitmap.getBitmap());
        String card1 = String.format(getString(R.string.cardDescription),
                model.myPlayer.getMyHand()[0].getCardRank().toString(),
                model.myPlayer.getMyHand()[0].getCardSuit().toString());
        handCards[0].setContentDescription(card1);

        handCardBitmap.update(cards[1][0], cards[1][1]);
        handCards[1].setImageBitmap(handCardBitmap.getBitmap());
        String card2 = String.format(getString(R.string.cardDescription),
                model.myPlayer.getMyHand()[1].getCardRank().toString(),
                model.myPlayer.getMyHand()[1].getCardSuit().toString());
        handCards[1].setContentDescription(card2);
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

            Bitmap communityCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards, 200, 300);
            ClientCard communityCardBitmap = new ClientCard(communityCardBitmap1, 0, 0, cards[0][0], cards[0][1]);

            for(int i = 0; i < 3; i++)
            {
                communityCardViews[i].setImageBitmap(communityCardBitmap.getBitmap());
                if(i!=2)
                {
                    communityCardBitmap.update(cards[i+1][0], cards[i+1][1]);
                }
                String card = String.format(getString(R.string.cardDescription),
                        model.getCommunityCards()[i].getCardRank().toString(),
                        model.getCommunityCards()[i].getCardSuit().toString());
                communityCardViews[i].setContentDescription(card);
            }
        }
    }

    public void setCommunityImageView()
    {

        if(communityCardViews[3].getDrawable() == null && model.getCommunityCards()[3] != null)
        {
            Log.d(TAG, "set t");
            Bitmap communityCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards, 200, 300);
            ClientCard communityCardBitmap = new ClientCard(communityCardBitmap1, 0, 0, model.getCommunityCards()[3].getCardSuit().ordinal(),model.getCommunityCards()[3].getCardRank().ordinal());

            communityCardViews[3].setImageBitmap(communityCardBitmap.getBitmap());

            String card = String.format(getString(R.string.cardDescription),
                    model.getCommunityCards()[3].getCardRank().toString(),
                    model.getCommunityCards()[3].getCardSuit().toString());
            communityCardViews[3].setContentDescription(card);
        }
        if(communityCardViews[4].getDrawable() == null && model.getCommunityCards()[4] != null)
        {
            Log.d(TAG, "set r");
            Bitmap communityCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards, 200, 300);
            ClientCard communityCardBitmap = new ClientCard(communityCardBitmap1, 0, 0, model.getCommunityCards()[4].getCardSuit().ordinal(),model.getCommunityCards()[4].getCardRank().ordinal());

            communityCardViews[4].setImageBitmap(communityCardBitmap.getBitmap());

            String card = String.format(getString(R.string.cardDescription),
                    model.getCommunityCards()[4].getCardRank().toString(),
                    model.getCommunityCards()[4].getCardSuit().toString());
            communityCardViews[4].setContentDescription(card);
        }
    }

    public void removeViews(ImageView[] views)
    {
        for(ImageView view : views)
        {
            view.setImageDrawable(null);
            view.setContentDescription(null);
        }
    }

    public void removeCommunityCards()
    {
        Log.d(TAG, "remove community cards");
        removeViews(communityCardViews);
    }

    public void removeHand()
    {
        removeViews(handCards);
    }

    public void updatePlayers()
    {
        List<PlayerUser> temp = model.getPlayers();
        PlayerUser tempPlayer;
        for(int i = 0; i < temp.size(); i++) {
            tempPlayer = temp.get(i);

            Bitmap playerDisplayBitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.player_display, 100, 100);

            Bitmap newBitmap = playerDisplayBitmap.copy(Bitmap.Config.ARGB_8888, true);

            Canvas c = new Canvas(newBitmap);
            c.drawBitmap(playerDisplayBitmap, 0, 0, null);

            Paint paint = new Paint();

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(50);

            Log.d(TAG, "" + tempPlayer.getCurrency());

            if(tempPlayer.getID() == model.myPlayer.getMyID())
            {
                c.drawText("YOU", 25, 65, paint);
                c.drawText(Integer.toString(tempPlayer.getCurrency()), 25, 105, paint);
                c.drawText(Integer.toString(tempPlayer.getCurrentBet()), 25, 145, paint);
            }
            else
            {
                c.drawText(tempPlayer.username, 25, 65, paint);
                c.drawText(Integer.toString(tempPlayer.getCurrency()), 25, 105, paint);
                c.drawText(Integer.toString(tempPlayer.getCurrentBet()), 25, 145, paint);
            }

            ClientPlayer playerBitmap = new ClientPlayer(newBitmap, 0, 0, 0, 0);
            playerDisplays[i].setImageDrawable(null);
            playerDisplays[i].setImageBitmap(playerBitmap.getBitmap());
            playerDisplayIDs[i] = tempPlayer.getID();

            String player = String.format(getString(R.string.playerDescription),
                    tempPlayer.getID(),
                    tempPlayer.getCurrency(),
                    tempPlayer.getCurrentBet());
            playerDisplays[i].setContentDescription(player);
        }
    }

    public void addPlayer()
    {

    }

    public void removePlayer(int id)
    {
        for(int i = 0; i < playerDisplayIDs.length; i++)
        {
            Log.d(TAG, "" + id + " " + playerDisplayIDs[i]);
            if(playerDisplayIDs[i] == id)
            {
                playerDisplays[i].setImageDrawable(null);
                playerDisplays[i].setContentDescription(null);
            }
        }
    }

    public void updatePlayerTurn(int id)
    {
        Log.d(TAG, "updating players turn");
        for(int i = 0; i < playerDisplayIDs.length; i++)
        {
            Log.d(TAG, "" + playerDisplayIDs[i] + " " + id);
            if(playerDisplayIDs[i] == id)
            {
                playerDisplayOverlays[i].setBackgroundResource(R.drawable.player_turn_animation);
                AnimationDrawable anim = (AnimationDrawable) playerDisplayOverlays[i].getBackground();
                anim.setOneShot(true);
                anim.setVisible(true, true);
                anim.start();

                myTurn = true;

                if(id == model.myPlayer.getMyID())
                {
                    turnTimer = 21;
                }
            }
        }
    }

    public void setNotTurn()
    {
        turnTime.setText(getString(R.string.notYourTurn));
        myTurn = false;
    }

    public void setAway(int id)
    {
        for(int i = 0; i < playerDisplayIDs.length; i++)
        {
            if(playerDisplayIDs[i] == id)
            {
                Bitmap playerDisplayBitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.player_display_away, 100, 100);

                Bitmap newBitmap = playerDisplayBitmap.copy(Bitmap.Config.ARGB_8888, true);

                Canvas c = new Canvas(newBitmap);
                c.drawBitmap(playerDisplayBitmap, 0, 0, null);

                Paint paint = new Paint();

                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(50);

                c.drawText("AWAY", 25, 65, paint);

                playerDisplays[i].setImageDrawable(null);
                playerDisplays[i].setImageBitmap(newBitmap);

                String playerAway = String.format(getString(R.string.playerAwayDescription),
                        id);
                playerDisplays[i].setContentDescription(playerAway);
            }

            if(id == model.myPlayer.getMyID())
            {
                myTurn = false;
            }
        }
    }

    public void broadcastMove(PlayerMove move)
    {
        PlayerUser temp = model.getPlayer(move.id);
        String moveString = String.format(getString(R.string.broadcastMove), temp.username, move.move);
        turnBroadcast.setText(moveString);
        updatePlayers();
    }

    public void displayWinners(List<PlayerUser> winners, int pot)
    {
        Log.d(TAG, "game ended");
        if(winners.size() != 0)
        {
            Resources res = getResources();
            String winnerNames = "";
            for(PlayerUser player : winners)
            {
                winnerNames = winnerNames.concat(player.username + " ");
            }
            String winString = String.format(res.getQuantityString(R.plurals.winOrWinners, winners.size(), winnerNames, (pot / winners.size())));

            turnBroadcast.setText(winString);
        }
    }
    public void updateSlider()
    {
        SeekBar slider = bet_slider_frag.getBetSlider();
        slider.setMax(model.myPlayer.getMyPlayer().getCurrency() - getMinValue());
        Log.d(TAG, Integer.toString(model.myPlayer.getMyPlayer().getCurrency() - getMinValue()));
    }

    public int getMinValue()
    {
        return model.bet.calcMinRaise();
    }

    public void updatePot(int pot)
    {
        String potString = String.format(getString(R.string.potString), pot);
        potDisplay.setText(potString);
    }

    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.leaveButton:
                    model.pressedButton(PlayerUserMove.EXIT, 0);
                    finish();
                    break;
                case R.id.communityCard1:
                    zoomCards(1);
                    break;
                case R.id.communityCard2:
                    zoomCards(1);
                    break;
                case R.id.communityCard3:
                    zoomCards(1);
                    break;
                case R.id.communityCard4:
                    zoomCards(1);
                    break;
                case R.id.communityCard5:
                    zoomCards(1);
                    break;
                case R.id.handCard1:
                    zoomCards(0);
                    break;
                case R.id.handCard2:
                    zoomCards(0);
                    break;
                default:
                    break;
            }
        }
    };

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
        getSupportFragmentManager().beginTransaction().show(call_button_frag).commitNow();
    }

    public void addCheckFrag()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.callcheckbutton, check_button_frag).commitNow();
        getSupportFragmentManager().beginTransaction().show(check_button_frag).commitNow();
    }

    public void addSliderFrag()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.betSliderLayout, bet_slider_frag).commitNow();
        getSupportFragmentManager().beginTransaction().show(bet_slider_frag).commitNow();
        updateSlider();
        sliderVisible = true;
        Thread updateBetAmountThread = new Thread( updateBetAmount );
    }

    private Runnable updateBetAmount = new Runnable() {
        @Override
        public void run() {
            while(sliderVisible)
            {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int amount = bet_slider_frag.getBetSlider().getProgress() + getMinValue();
                        betSliderAmount.setText(amount);
                        betSliderAmount.setContentDescription(getString(R.string.betAmountDescription));
                    }
                });
                try
                {
                    Thread.sleep(500);
                }
                catch(InterruptedException e)
                {

                }
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    betSliderAmount.setText(null);
                    betSliderAmount.setContentDescription(null);
                }
            });
        }
    };

    public void hideCallFrag()
    {
        getSupportFragmentManager().beginTransaction().hide(call_button_frag).commitNow();
    }

    public void hideCheckFrag()
    {
        getSupportFragmentManager().beginTransaction().hide(check_button_frag).commitNow();
    }

    public void hideSliderFrag()
    {
        getSupportFragmentManager().beginTransaction().hide(bet_slider_frag).commitNow();
        sliderVisible = false;
    }

    public void zoomCards(int type)
    {
        Zoomed_Cards zoomFrag = Zoomed_Cards.newInstance(type);
        getSupportFragmentManager().beginTransaction().replace(R.id.zoomCardsHolder, zoomFrag).commitNow();
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

}
