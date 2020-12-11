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
import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.Hand;
import com.game.poker.psymw6mobilepokerapp.PokerAppRunnable.GameListener;
import com.game.poker.psymw6mobilepokerapp.PokerAppService.ServerConnectionService;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments.Bet_Slider;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments.Call_Button;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments.Check_Button;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments.Zoomed_Cards;
import com.game.poker.psymw6mobilepokerapp.R;

import java.util.HashMap;
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
    private TextView betSliderAmountDisplay;

    private int[] playerDisplayIDs;

    private boolean stopThreads = false;
    private boolean myTurn = false;
    private int turnTimer = 0;
    private boolean sliderVisible = false;

    private Handler handler;

    public static final String TAG = "gameView";

    /**
     * Setting up the views
     */
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

        betSliderAmountDisplay = findViewById(R.id.betSliderAmountView);

        playerDisplayIDs = new int[5];

        handler = new Handler();
    }

    /**
     * Binds to service, creates threads for communication with server and a turn timer thread
     * Threads terminate on leaving game
     */
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

                    GameListener listener = new GameListener(serviceInstance.getClientSocket(), serviceInstance.getIn(), queue, GameView.this);
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

                        }
                    }
                }
            }
        });
        monitor.start();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        stopThreads();
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
        stopThreads();
    }

    /**
     * Stops the threads
     */
    public void stopThreads()
    {
        stopThreads = true;
    }

    /**
     * Display hand cards in bottom left of activity
     */
    public void setCardImageViews()
    {
        int[][] cards = new int[2][2];

        for(int i = 0; i < 2; i++)
        {
            cards[i][0] = model.myPlayer.getMyHand()[i].getCardSuit().ordinal();
            cards[i][1] = model.myPlayer.getMyHand()[i].getCardRank().ordinal();
        }

        Bitmap handCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards, 440, 600);
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

    /**
     * Display the flop community cards in middle of activity
     */
    public void setCommunityImageViews()
    {
        if(model.getCommunityCards()[0] != null)
        {
            int[][] cards = new int[3][2];

            for(int i = 0; i < 3; i++) {
                cards[i][0] = model.getCommunityCards()[i].getCardSuit().ordinal();
                cards[i][1] = model.getCommunityCards()[i].getCardRank().ordinal();
            }

            Bitmap communityCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards, 300, 500);
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

    /**
     * Display turn and/or river after flop cards
     */
    public void setCommunityImageView()
    {

        if(communityCardViews[3].getDrawable() == null && model.getCommunityCards()[3] != null)
        {
            Bitmap communityCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards, 300, 500);
            ClientCard communityCardBitmap = new ClientCard(communityCardBitmap1, 0, 0, model.getCommunityCards()[3].getCardSuit().ordinal(),model.getCommunityCards()[3].getCardRank().ordinal());

            communityCardViews[3].setImageBitmap(communityCardBitmap.getBitmap());

            String card = String.format(getString(R.string.cardDescription),
                    model.getCommunityCards()[3].getCardRank().toString(),
                    model.getCommunityCards()[3].getCardSuit().toString());
            communityCardViews[3].setContentDescription(card);
        }
        if(communityCardViews[4].getDrawable() == null && model.getCommunityCards()[4] != null)
        {
            Bitmap communityCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards, 300, 500);
            ClientCard communityCardBitmap = new ClientCard(communityCardBitmap1, 0, 0, model.getCommunityCards()[4].getCardSuit().ordinal(),model.getCommunityCards()[4].getCardRank().ordinal());

            communityCardViews[4].setImageBitmap(communityCardBitmap.getBitmap());

            String card = String.format(getString(R.string.cardDescription),
                    model.getCommunityCards()[4].getCardRank().toString(),
                    model.getCommunityCards()[4].getCardSuit().toString());
            communityCardViews[4].setContentDescription(card);
        }
    }

    /**
     * Removes images from the supplied imageviews to display empty space
     *
     * @param views The views to remove
     */
    public void removeViews(ImageView[] views)
    {
        for(ImageView view : views)
        {
            view.setImageDrawable(null);
            view.setContentDescription(null);
        }
    }

    /**
     * Removes the community cards from the activity view
     */
    public void removeCommunityCards()
    {
        removeViews(communityCardViews);
    }

    /**
     * Removes the hand cards from the activity view
     */
    public void removeHand()
    {
        removeViews(handCards);
    }

    /**
     * Draws player text over player display image and displays at top of screen
     */
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

            if(tempPlayer.getID() == model.myPlayer.getMyID())
            {
                c.drawText("YOU", 15, 65, paint);
                c.drawText(Integer.toString(tempPlayer.getCurrency()), 15, 110, paint);
                c.drawText(Integer.toString(tempPlayer.getCurrentBet()), 15, 155, paint);
            }
            else
            {
                c.drawText(tempPlayer.username, 15, 65, paint);
                c.drawText(Integer.toString(tempPlayer.getCurrency()), 15, 110, paint);
                c.drawText(Integer.toString(tempPlayer.getCurrentBet()), 15, 155, paint);
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

    /**
     * Removes a player from the view
     *
     * @param id The ID of the player to remove
     */
    public void removePlayer(int id)
    {
        for(int i = 0; i < playerDisplayIDs.length; i++)
        {
            if(playerDisplayIDs[i] == id)
            {
                playerDisplays[i].setImageDrawable(null);
                playerDisplays[i].setContentDescription(null);
            }
        }
    }

    /**
     * Plays an animation representing the turn countdown visually around the player frame and sets the turn timer to countdown
     *
     * @param id The ID of the player whose turn it is
     */
    public void updatePlayerTurn(int id)
    {
        for(int i = 0; i < playerDisplayIDs.length; i++)
        {
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

    /**
     * Sets it to no longer be the client's turn
     */
    public void setNotTurn()
    {
        turnTime.setText(getString(R.string.notYourTurn));
        myTurn = false;
    }

    /**
     * Replaces the view of the player with an away image when a player misses a turn
     *
     * @param id The ID of the player to set away
     */
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

    /**
     * Broadcasts the received move on the activity screen as text
     *
     * @param move The move to broadcast
     */
    public void broadcastMove(PlayerMove move)
    {
        PlayerUser temp = model.getPlayer(move.id);
        String moveString = String.format(getString(R.string.broadcastMove), temp.username, move.move);
        turnBroadcast.setText(moveString);
        updatePlayers();
    }

    /**
     * Displays the winners on the activity screen as text
     *
     * @param winners The list of winners
     * @param pot The amount won
     */
    public void displayWinners(HashMap<PlayerUser, Hand> winners, int pot)
    {
        PlayerUser temp = null;
        if(winners.size() != 0)
        {
            Resources res = getResources();
            String winnerNames = "";
            for(PlayerUser player : winners.keySet())
            {
                winnerNames = winnerNames.concat(player.username + " ");
                temp = player;
            }
            String winString = String.format(res.getQuantityString(R.plurals.winOrWinners, winners.size(), winnerNames, (pot / winners.size()), winners.get(temp)));

            turnBroadcast.setText(winString);
        }
    }

    /**
     * Updates the max value possible for the bet slider
     */
    public void updateSlider()
    {
        SeekBar slider = bet_slider_frag.getBetSlider();
        slider.setMax(model.myPlayer.getMyPlayer().getCurrency() - getMinValue());
    }

    /**
     * Getter for the minimum value that must be bet or raised by
     *
     * @return Int value of the min next raise that can be made
     */
    public int getMinValue()
    {
        return model.bet.calcMinRaise();
    }

    /**
     * Updates the pot display in the activity to the new amount
     *
     * @param pot The amount in the pot
     */
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
                    stopThreads();
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

    /**
     *
     *
     * @param options Options object for the bitmap
     * @param reqWidth The width to scale to
     * @param reqHeight The height to scale to
     * @return The sample size required for the input width and height
     */
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

    /**
     *
     * @param res The resource object
     * @param resID The resource to create bitmap from
     * @param reqWidth The width of the bitmap
     * @param reqHeight The height of the bitmap
     * @return A scaled bitmap from the given resource
     */
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

    /**
     * Adds the call button fragment to the activity
     */
    public void addCallFrag()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.callcheckbutton, call_button_frag).commitNow();
        getSupportFragmentManager().beginTransaction().show(call_button_frag).commitNow();
    }

    /**
     * Adds the check button fragment to the activity
     */
    public void addCheckFrag()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.callcheckbutton, check_button_frag).commitNow();
        getSupportFragmentManager().beginTransaction().show(check_button_frag).commitNow();
    }

    /**
     * Adds the bet slider fragment to the activity
     */
    public void addSliderFrag()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.betSliderLayout, bet_slider_frag).commitNow();
        getSupportFragmentManager().beginTransaction().show(bet_slider_frag).commitNow();
        updateSlider();
        sliderVisible = true;
        Thread updateBetAmountThread = new Thread( updateBetAmount );
        updateBetAmountThread.start();
    }

    /**
     * Thread for giving a visual representation of the slider value
     */
    private Runnable updateBetAmount = new Runnable() {
        @Override
        public void run() {
            while(sliderVisible)
            {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int amount = bet_slider_frag.getBetSlider().getProgress() + getMinValue();
                        betSliderAmountDisplay.setText("" + amount);
                        betSliderAmountDisplay.setContentDescription(getString(R.string.betAmountDescription));
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
                    betSliderAmountDisplay.setText(null);
                    betSliderAmountDisplay.setContentDescription(null);
                }
            });
        }
    };

    /**
     * Hides the call button fragment from the activity
     */
    public void hideCallFrag()
    {
        getSupportFragmentManager().beginTransaction().hide(call_button_frag).commitNow();
    }

    /**
     * Hides the check button fragment from the activity
     */
    public void hideCheckFrag()
    {
        getSupportFragmentManager().beginTransaction().hide(check_button_frag).commitNow();
    }

    /**
     * Hides the bet slider fragment from the activity
     */
    public void hideSliderFrag()
    {
        getSupportFragmentManager().beginTransaction().hide(bet_slider_frag).commitNow();
        sliderVisible = false;
    }

    /**
     * Adds the zoomed cards fragment to the activity
     */
    public void zoomCards(int type)
    {
        Zoomed_Cards zoomFrag = Zoomed_Cards.newInstance(type);
        getSupportFragmentManager().beginTransaction().replace(R.id.zoomCardsHolder, zoomFrag).commitNow();
    }

    /**
     * Getter for the button actions object
     *
     * @return A GameViewActions object
     */
    public GameViewActions getActions()
    {
        return actions;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceBinder = (ServerConnectionService.ServerBinder) service;
            serviceInstance = serviceBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
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
