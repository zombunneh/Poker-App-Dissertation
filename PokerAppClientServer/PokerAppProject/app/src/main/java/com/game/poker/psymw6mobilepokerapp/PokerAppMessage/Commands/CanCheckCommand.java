package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameViewModel;

public class CanCheckCommand implements Command{

    private static final long serialVersionUID = 1923486483L;

    /**
     * Sends a notification that client move options are fold, check or bet
     */
    public CanCheckCommand()
    {

    }

    /**
     * Uses the supplied invoker to update the model to CHECK state
     *
     * @param invoker The invoker that will execute the command
     */
    public void execute(CommandInvoker invoker) {
        invoker.getModel().updateState(GameViewModel.State.CHECK);
    }
}
