package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import java.util.Comparator;

/**
 * enum outlining the different potential hands for a player in order of ascending rank for easier comparison
 */
public enum Hand implements Comparable<Hand>{
    HIGH_CARD,
    PAIR,
    TWO_PAIR,
    THREE_KIND,
    STRAIGHT,
    FLUSH,
    FULL_HOUSE,
    FOUR_KIND,
    STRAIGHT_FLUSH,
    ROYAL_FLUSH
}

/**
 * Class implements comparator in order to evaluate hands in the EvaluateHand class
 */
class HandComparator implements Comparator<Hand> {
    @Override
    public int compare(Hand o, Hand o2) {
        if(o.ordinal() == o2.ordinal())
        {
            return 0;
        }
        else if(o.ordinal() < o2.ordinal())
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}