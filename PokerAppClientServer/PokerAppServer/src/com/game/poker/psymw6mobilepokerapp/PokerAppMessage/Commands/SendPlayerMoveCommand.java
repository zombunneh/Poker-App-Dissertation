package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerMove;


public class SendPlayerMoveCommand implements Command{

    private static final long serialVersionUID = 2340594748L;
    private PlayerMove move;

    /**
     * Sends a PlayerMove to clients
     *
     * @param move
     */
    public SendPlayerMoveCommand(PlayerMove move)
    {
        this.move = move;
    }

    /**
     * Implemented client side
     *
     * @param invoker Invoker to execute commands
     */
    public void execute(CommandInvoker invoker) {

    }
}
