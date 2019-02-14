package com.game.poker.psymw6mobilepokerapp.PokerAppObjects;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class EvaluateHand {
    private List<Card> cardsForHand;
    private HashMap<PlayerUser, Hand> evaluatedHands;
    private Card highCard;
    private Card highCard2;
    private Card kicker;

    public static final int NUMBER_OF_SUITS = 4;

    /**
    Evaluating hands at the end of a round.
    Potentially add function to evaluate hands at each stage to provide player with visual feedback on the state of their hand
    as the com.game progresses
     */
    public EvaluateHand()
    {
        evaluatedHands = new HashMap<>();
    }

    /**
    returns a Card array combination of community cards and the hand of the current player to evaluate
    TODO remember to make private after testing
     */
    public Card[] createFullHand(PlayerUser currentPlayer, List<Card> communityCards)
    {
        Card[] temp = new Card[7];
        for(int i = 0; i <5; i++)
        {
            temp[i] = communityCards.get(i);
        }
        for(int i = 0; i < 2; i++)
        {
            temp[i+5] = currentPlayer.getHand()[i];
        }
        return temp;
    }

    /**
    counting sort implementation to sort hand
    change to private after done testing
     */
    public void sortHand(Card[] handToSort)
    {
        // 'k' in algorithm -> max range of values
        int max = 13;
        Card[] tempHand = new Card[handToSort.length];

        //updating count values
        int[] count = new int[max];
        for(int i = 0; i < handToSort.length; i++)
        {
            count[handToSort[i].getCardRank().ordinal()]++;
        }

        //updating to cumulative count values
        for(int i = 1; i < max; ++i)
        {
            count[i] += count[i - 1];
        }

        //sorting hand into correct spots
        for(int i = 0; i < handToSort.length; i++)
        {
            tempHand[count[handToSort[i].getCardRank().ordinal()]-1] = handToSort[i];
            count[handToSort[i].getCardRank().ordinal()]--;
        }

        //copying sorted hand back across
        for(int i = 0; i < handToSort.length; i++)
        {
            handToSort[i] = tempHand[i];
        }
    }

    /*
    returns a hashmap of each player user and their evaluated hand
     */
    public int handEvaluator(List<PlayerUser> playersToEval, List<Card> communityCards)
    {
        ListIterator<PlayerUser> iterator = playersToEval.listIterator();
        PlayerUser tempPlayer;
        Card[] tempHand;
        Hand currentHand;
        while(iterator.hasNext())
        {
            tempPlayer = iterator.next();

            tempHand = createFullHand(tempPlayer, communityCards);
            sortHand(tempHand);
            currentHand = checkHand(tempHand);
            if(kicker != null)
            {
                tempPlayer.setKicker(kicker);
            }
            if(highCard != null)
            {
                tempPlayer.setHighCard(highCard);
            }
            if(highCard2 != null)
            {
                tempPlayer.setHighCard2(highCard2);
            }
            evaluatedHands.put(tempPlayer, currentHand);
        }
        //sort evaluated hands
        //return strongest hand/hands (winners)
        evaluatedHands.forEach(((playerUser, hand) -> System.out.println(playerUser.username + " " + hand.toString())));
        return 0;
    }

    /**
    takes hashmap of player/hand and sort them and return the winner(s)
    TODO COMPLETE
     */
    public int getHandWinner()
    {
     return 0;
    }

    /**
    royal flush has no tie break
    straight flush ties with same high card suit doesn't matter, no kicker used
    TODO remember to set high card/kicker where applicable #!!!1
     */
    public Hand checkHand(Card[] handToCheck)
    {
        if(checkRoyalFlush(handToCheck))
        {
            return Hand.ROYAL_FLUSH;
        }
        else if(checkStraightFlush(handToCheck))
        {
            highCard = cardsForHand.get(0);
            return Hand.STRAIGHT_FLUSH;
        }
        else if(checkOfKind(handToCheck, 4))
        {
            highCard = cardsForHand.get(0);
            for(int i = handToCheck.length - 1; i >= 0; i--)
            {
                if(!cardsForHand.contains(handToCheck[i]))
                {
                    kicker = handToCheck[i];
                }
            }
            return Hand.FOUR_KIND;
        }
        else if(checkFullHouse(handToCheck))
        {
            for(int i = handToCheck.length - 1; i >= 0; i--)
            {
                if(handToCheck[i].getCardRank().ordinal() != highCard.getCardRank().ordinal())
                {
                    highCard2 = handToCheck[i];
                }
            }
            return Hand.FULL_HOUSE;
        }
        else if(checkFlush(handToCheck))
        {
            return Hand.FLUSH;
        }
        else if(checkStraight(handToCheck))
        {
            highCard = cardsForHand.get(0);
            return Hand.STRAIGHT;
        }
        else if(checkOfKind(handToCheck, 3))
        {
            highCard = cardsForHand.get(0);
            return Hand.THREE_KIND;
        }
        else if(checkPair(handToCheck, 2))
        {
            return Hand.TWO_PAIR;
        }
        else if(checkPair(handToCheck, 1))
        {
            return Hand.PAIR;
        }
        else
        {
            return Hand.HIGH_CARD;
        }
    }

    /**
    checks if hand is a royal flush by:
        iterating through all 4 suits in outer FOR loop
            iterating through all cards in hand in inner FOR loop
        check if current cards suit matches current suit from outer FOR loop else increment n
            check if current card is the next card in royal flush sequence else increment n
     */
    private boolean checkRoyalFlush(Card[] handToCheck)
    {
        int rfCardCount = 0;
        int n;
        for(int i = 0; i < NUMBER_OF_SUITS; i++)
        {
            cardsForHand = new ArrayList<>();
            n = 5;
            for(int j = 0; j < handToCheck.length; j++)
            {
                if(handToCheck[j].getCardSuit().ordinal() == i)
                {
                    if(handToCheck[j].getCardRank().ordinal() == Card.Rank.values().length - n + j)
                    {
                        cardsForHand.add(handToCheck[j]);
                        rfCardCount++;
                        if(rfCardCount == 5)
                        {
                            return true;
                        }
                    }
                    else
                    {
                        n++;
                    }
                }
                else
                {
                    n++;
                }
            }
            cardsForHand = null;
            rfCardCount = 0;
        }
        return false;
    }

    /**
    check if hand is a straight flush by:
        iterating through all 4 suits in outer FOR loop
     */
    private boolean checkStraightFlush(Card[] handToCheck)
    {
        int sfCardCount = 0;
        int n = 1;
        for(int i = 0; i < NUMBER_OF_SUITS; i++)
        {
            cardsForHand = new ArrayList<>();
            for(int j = handToCheck.length - 1; j >= 1; j--)
            {
                if(handToCheck[j].getCardSuit().ordinal() == i)
                {
                    while(handToCheck[j].getCardSuit().ordinal() != handToCheck[j-n].getCardSuit().ordinal())
                    {
                        n++;
                        if(j - n == -1)
                        {
                            n--;
                            break;
                        }
                    }

                    if(handToCheck[j].getCardRank().ordinal() == handToCheck[j-n].getCardRank().ordinal() + 1 && handToCheck[j].getCardSuit().ordinal() == handToCheck[j-n].getCardSuit().ordinal())
                    {
                        cardsForHand.add(handToCheck[j]);
                        sfCardCount++;
                        if(sfCardCount == 4)
                        {
                            cardsForHand.add(handToCheck[j-n]);
                            return true;
                        }
                    }
                    else
                    {
                        sfCardCount = 0;
                    }
                    //additional checks for ace LOW straight flush
                    if(handToCheck[j-n].getCardRank() == Card.Rank.TWO && sfCardCount == 3)
                    {
                        if(handToCheck[4].getCardRank() == Card.Rank.ACE && handToCheck[4].getCardSuit().ordinal() == i)
                        {
                            cardsForHand.add(handToCheck[4]);
                            sfCardCount++;
                        }
                        else if(handToCheck[5].getCardRank() == Card.Rank.ACE && handToCheck[5].getCardSuit().ordinal() == i)
                        {
                            cardsForHand.add(handToCheck[5]);
                            sfCardCount++;
                        }
                        else if(handToCheck[6].getCardRank() == Card.Rank.ACE && handToCheck[6].getCardSuit().ordinal() == i)
                        {
                            cardsForHand.add(handToCheck[6]);
                            sfCardCount++;
                        }
                        else
                        {
                            sfCardCount = 0;
                        }
                        if(sfCardCount == 4)
                        {
                            return true;
                        }
                    }
                    //reset n for next loop
                    n = 1;
                }

            }
            cardsForHand = null;
            sfCardCount = 0;
        }
        return false;
    }

    /**
     * check if hand is a three/four of a kind by:
     * @param handToCheck The hand to evaluate
     * @param numberOfKind The number of cards of the same rank to check for
     * @return Returns whether the hand was found or not
     */
    private boolean checkOfKind(Card[] handToCheck, int numberOfKind)
    {
        int nKind = 0;
        for(int i = Card.Rank.values().length - 1; i >= 0; i--)
        {
            cardsForHand = new ArrayList<>();
            for(int j = 0; j < handToCheck.length; j++)
            {
                if(handToCheck[j].getCardRank() == Card.Rank.values()[i]) {
                    cardsForHand.add(handToCheck[j]);
                    nKind++;
                }
                if(nKind == numberOfKind)
                {
                    return true;
                }
            }
            cardsForHand = null;
            nKind = 0;
        }

        return false;
    }

    /*
    check if hand is a full house by:
     */
    private boolean checkFullHouse(Card[] handToCheck)
    {
        boolean tKindCount = false;
        boolean pCount = false;
        cardsForHand = new ArrayList<>();
        for(int j = handToCheck.length - 1; j >= 1; j--)
        {
            if(handToCheck[j].getCardRank().ordinal() == handToCheck[j-1].getCardRank().ordinal())
            {
                if(!pCount)
                {
                    cardsForHand.add(handToCheck[j]);
                    cardsForHand.add(handToCheck[j-1]);
                    pCount = true;
                }
                else if(pCount)
                {
                    cardsForHand.add(handToCheck[j-1]);
                    highCard = handToCheck[j-1];
                    tKindCount = true;
                    pCount = false;
                }
                if(tKindCount && pCount)
                {
                    return true;
                }
            }
        }
        highCard = null;
        return false;
    }

    /**
    check if hand is a flush by:
        iterating through all 4 suits in outer FOR loop
            iterating through all cards in hand in inner FOR loop
            if current card suit is equal to current suit in outer FOR loop add to list and increment fCardCount
            if fCardCount reaches 5 hand is a flush
     */
    private boolean checkFlush(Card[] handToCheck)
    {
        int fCardCount = 0;
        for(int i = 0; i < NUMBER_OF_SUITS; i++)
        {
            cardsForHand = new ArrayList<>();
            for(int j = handToCheck.length - 1; j >= 0; j--)
            {
                if(handToCheck[j].getCardSuit().ordinal() == i)
                {
                    cardsForHand.add(handToCheck[j]);
                    fCardCount++;
                }
                if(fCardCount == 5)
                {
                    return true;
                }
            }
            cardsForHand = null;
            fCardCount = 0;
        }

        return false;
    }

    /**
    check if hand is a straight by:

     */
    private boolean checkStraight(Card[] handToCheck)
    {
        int sCardCount = 0;
        cardsForHand = new ArrayList<>();
        for(int i = handToCheck.length - 1; i >= 1; i--)
        {
            if(handToCheck[i].getCardRank().ordinal() == handToCheck[i-1].getCardRank().ordinal() + 1)
            {
                cardsForHand.add(handToCheck[i]);
                sCardCount++;
                if(sCardCount == 4)
                {
                    cardsForHand.add(handToCheck[i+1]);
                    return true;
                }
            }
            else
            {
                sCardCount = 0;
            }
            //additional checks for ace LOW straight flush
            if(handToCheck[i].getCardRank() == Card.Rank.TWO && sCardCount == 3)
            {

                if(handToCheck[4].getCardRank() == Card.Rank.ACE)
                {
                    cardsForHand.add(handToCheck[4]);
                    sCardCount++;
                }
                else if(handToCheck[5].getCardRank() == Card.Rank.ACE)
                {
                    cardsForHand.add(handToCheck[5]);
                    sCardCount++;
                }
                else if(handToCheck[6].getCardRank() == Card.Rank.ACE)
                {
                    cardsForHand.add(handToCheck[6]);
                    sCardCount++;
                }
                else
                {
                    sCardCount = 0;
                }
                if(sCardCount == 4)
                {
                    return true;
                }
            }

        }
        return false;
    }

    /**
    check if hand is a pair/two pair by:
        iterating through all cards in hand
        if next card is same as current card a pair is found
     */
    private boolean checkPair(Card[] handToCheck, int numberOfPair)
    {
        cardsForHand = new ArrayList<>();
        int numPairs = 0;
        for(int i = handToCheck.length - 1; i >= 1; i--)
        {
            if(handToCheck[i].getCardRank().ordinal() == handToCheck[i-1].getCardRank().ordinal())
            {
                cardsForHand.add(handToCheck[i]);
                cardsForHand.add(handToCheck[i-1]);
                if(numberOfPair == 1)
                {
                    return true;
                }
                else
                {
                    numPairs++;
                }
                if(numberOfPair == 2 && numPairs == 2)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
