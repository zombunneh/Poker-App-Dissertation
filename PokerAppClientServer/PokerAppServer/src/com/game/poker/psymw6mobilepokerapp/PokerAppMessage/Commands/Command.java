package com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Commands;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.ClientOnly.CommandInvoker;

import java.io.Serializable;

public interface Command extends Serializable {

    public void execute(CommandInvoker invoker);
}
