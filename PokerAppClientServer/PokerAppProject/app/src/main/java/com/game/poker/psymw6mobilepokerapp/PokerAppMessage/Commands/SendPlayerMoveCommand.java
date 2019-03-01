package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerMove;


public class SendPlayerMoveCommand implements Command{
    private PlayerMove move;

    public SendPlayerMoveCommand(PlayerMove move)
    {
        this.move = move;
    }

    public void execute(CommandInvoker invoker) {

    }
}
