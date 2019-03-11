package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerMove;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUserMove;


public class SendPlayerMoveCommand implements Command{

    private static final long serialVersionUID = 2340594748L;
    private PlayerMove move;

    public SendPlayerMoveCommand(PlayerMove move)
    {
        this.move = move;
    }

    public void execute(CommandInvoker invoker) {
        if(move.move == PlayerUserMove.BLIND)
        {
            invoker.getModel().bet.setBlind(move.bet);
        }
        invoker.getModel().lastTurn(move);
        for(PlayerUser player : invoker.getModel().getPlayers())
        {
            if(player.getID() == move.id
                    && move.move != PlayerUserMove.CHECK
                    && move.move != PlayerUserMove.EXIT
                    && move.move != PlayerUserMove.AWAY
                    && move.move != PlayerUserMove.FOLD)
            {
                player.setCurrentBet(move.bet);
            }
        }
    }
}
