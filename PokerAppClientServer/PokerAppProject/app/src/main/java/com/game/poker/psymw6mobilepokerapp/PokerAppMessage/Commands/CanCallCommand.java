package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameViewModel;

public class CanCallCommand implements Command{

    public CanCallCommand()
    {

    }

    public void execute(CommandInvoker invoker) {
        invoker.getModel().updateState(GameViewModel.State.CALL);
    }
}
