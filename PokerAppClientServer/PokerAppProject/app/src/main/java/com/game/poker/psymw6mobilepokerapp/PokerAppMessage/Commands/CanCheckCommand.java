package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameViewModel;

public class CanCheckCommand implements Command{
    public CanCheckCommand()
    {

    }

    public void execute(CommandInvoker invoker) {
        invoker.model.updateState(GameViewModel.State.CALL);
    }
}
