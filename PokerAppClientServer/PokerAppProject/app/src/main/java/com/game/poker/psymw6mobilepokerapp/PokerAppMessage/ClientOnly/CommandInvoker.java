package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly;

import com.game.poker.psymw6mobilepokerapp.PokerAppShared.GameView;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.GameViewController;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.GameViewModel;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class CommandInvoker implements Runnable{
    public final GameViewModel model;
    public final GameViewController controller;
    public final GameView view;
    private CommandQueue queue;

    public CommandInvoker(Socket client, ObjectOutputStream out, CommandQueue queue )
    {
        this.model = new GameViewModel(client, out);
        this.controller = new GameViewController();
        this.view = new GameView();
        this.queue = queue;
    }

    @Override
    public void run() {

    }
}
