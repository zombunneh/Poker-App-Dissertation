package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

import java.io.Serializable;

public interface Command extends Serializable {

    /**
     * Interface for Command design pattern
     *
     * @param invoker Will execute the commands on the client side
     */
    public void execute(CommandInvoker invoker);
}
