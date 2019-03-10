package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameViewModel;

public class CanCheckCommand implements Command{

    private static final long serialVersionUID = 1923486483L;

    public CanCheckCommand()
    {

    }

    public void execute(CommandInvoker invoker) {
        invoker.getModel().updateState(GameViewModel.State.CHECK);
    }
}
