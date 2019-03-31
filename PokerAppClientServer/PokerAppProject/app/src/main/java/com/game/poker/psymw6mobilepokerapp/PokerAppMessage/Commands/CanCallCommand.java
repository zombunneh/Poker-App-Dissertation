package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameViewModel;

public class CanCallCommand implements Command{

    /**
     * Sends a notification that client move options are fold, call or raise
     */
    public CanCallCommand()
    {

    }

    /**
     * Uses the supplied invoker to update the model to CALL state
     *
     * @param invoker The invoker that will execute the command
     */
    public void execute(CommandInvoker invoker) {
        invoker.getModel().updateState(GameViewModel.State.CALL);
    }
}
