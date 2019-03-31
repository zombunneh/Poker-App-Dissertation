package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

public class CanCheckCommand implements Command{

    private static final long serialVersionUID = 1923486483L;

    /**
     * Sends a notification that client move options are fold, check or bet
     */
    public CanCheckCommand()
    {

    }

    /**
     * Implemented client side
     *
     * @param invoker Invoker to execute commands
     */
    public void execute(CommandInvoker invoker) {

    }
}
