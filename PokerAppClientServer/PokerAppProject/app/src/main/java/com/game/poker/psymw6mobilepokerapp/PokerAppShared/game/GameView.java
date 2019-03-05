package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandQueue;
import com.game.poker.psymw6mobilepokerapp.PokerAppRunnable.GameListener;
import com.game.poker.psymw6mobilepokerapp.PokerAppService.ServerConnectionService;
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

    private Call_Button call_button_frag;
    private Check_Button check_button_frag;
    public Bet_Slider bet_slider_frag;

    public static final String TAG = "gameView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        call_button_frag = Call_Button.newInstance();
        check_button_frag = Check_Button.newInstance();
        bet_slider_frag = Bet_Slider.newInstance();
        addSliderFrag();
        //addCallFrag();
        setContentView(R.layout.game_view_activity);
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

                    while (invoker.isInvoked() && listener.isRunning()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {

                        }
                    }
                }
            }
        });
        monitor.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(connection!= null)
        {
            unbindService(connection);
            connection = null;
        }
        if(serviceInstance != null)
        {
            serviceInstance = null;
        }
        Log.d(TAG, "activity stopped");
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

}
