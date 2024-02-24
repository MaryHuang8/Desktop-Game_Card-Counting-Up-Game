package Player;

import Core.CountingUpGame;
import Core.Rank;
import Core.Score;
import Core.Validator;
import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CleverPlayer extends ComputerPlayer {

    private static final int MIN_AGGRESSION_CARDS = 3;
    private static final int MY_MIN_AGGRESSION_CARDS = 2;

    private static final double HIGH_RISK_PROBABILITY = 0.75;
    private static final double LOW_RISK_PROBABILITY = 0.30;

    @Override
    public Card getCardOrSkip(Card prevCard) {
        return decideMove(prevCard, CountingUpGame.getCardsPlayed());
    }

    private Card decideMove(Card prevCard, List<Card> cardPlayed) {
        Card selectedCard = getLowestCardOrSkip(prevCard);
        List<Card> othersValidUnplayedCards = getOthersValidUnplayedCards(selectedCard);
        //Card othersHighestCard = getHighestRankCard(true, othersValidUnplayedCards);
        Card myHighestCard = getValidRankedCard(true, prevCard);
        Card mySecondHighestCard = getValidRankedCard(false, prevCard);

        int potScore = Score.calculateScore(cardPlayed);


        if (myHighestCard != null && potScore > 75 && !othersCanWinPotIfIPlaySelected(myHighestCard)) {
            selectedCard = myHighestCard;
        } else if (mySecondHighestCard != null && potScore > 25) {
            selectedCard = mySecondHighestCard;
        }
        if (selectedCard == myHighestCard && othersCanWinPotIfIPlaySelected(myHighestCard)){
            selectedCard = null;
        }
        return selectedCard;
    }


    /**
     * Calculates the number of cards of same rank to input card in the input list.
     * @param card
     * @param cardList
     * @return numSameRankCard
     */
    private int getNumSameRankCard(Card card, List<Card> cardList) {

        int num = 0;
        for (int i = 1; i < cardList.size(); i++) {
            if (Validator.rankEqual(cardList.get(i), card)) {
                num += 1;
            }
        }
        return num;
    }


    /**
     * Calculates the number of cards of higher rank and same suit to input card in the input cardList.
     * Assume card not in cardList.
     * @param card
     * @param cardList
     * @return numHigherRankSameSuitCard
     */
    private int getNumHigherRankSameSuitCard(Card card, List<Card> cardList) {

        int num = 0; //= getOthersValidUnplayedCards(card).size() - getNumSameRankCard(card, cardList);
        for (int i = 1; i < cardList.size(); i++) {
            if (Validator.rankGreaterThanOrEqual(cardList.get(i), card) && Validator.suitEqual(cardList.get(i), card)){
                num += 1;
            }
        }
        return num;
    }

    private Card getValidRankedCard(boolean findHighest, Card prevCard) {
        List<Card> cardList = Validator.getValidCardList(prevCard, hand.getCardList());
        if (cardList.isEmpty()) {
            return null;
        }

        Card highestCard = cardList.get(0);
        Card secondHighestCard = cardList.get(0);
        for (int i = 1; i < cardList.size(); i++) {
            if (Validator.checkValidation(highestCard, cardList.get(i))) {
                secondHighestCard = highestCard;
                highestCard = cardList.get(i);
            } else if (!findHighest && Validator.checkValidation(secondHighestCard, cardList.get(i))) {
                secondHighestCard = cardList.get(i);
            }
        }
        return findHighest ? highestCard : secondHighestCard;

    }

    private Card getHighestRankCard(boolean findHighest, List<Card> cardList) {
        if (cardList.isEmpty()) {
            return null;
        }

        Card highestCard = cardList.get(0);
        Card secondHighestCard = cardList.get(0);
        for (int i = 1; i < cardList.size(); i++) {
            if (Validator.rankGreaterThanOrEqual(highestCard, cardList.get(i))) {
                secondHighestCard = highestCard;
                highestCard = cardList.get(i);
            } else if (!findHighest && Validator.rankGreaterThanOrEqual(secondHighestCard, cardList.get(i))) {
                secondHighestCard = cardList.get(i);
            }
        }
        return findHighest ? highestCard : secondHighestCard;
    }

    /**
     * Calculates and gets the list of all valid cards others are able to play if selectedCard is played.
     * @param selectedCard
     * @return othersValidCardsIfPlaySelected
     */
    private List<Card> getOthersValidUnplayedCards(Card selectedCard){
        return Validator.getValidCardList(selectedCard, getOtherUnplayedCards());
    }


    /**
     * Gets the list of total cards still in other players' hands unplayed by calculations.
     * @return unPlayedCards
     */
    private List<Card> getOtherUnplayedCards(){

        List<Card> totalCardsPlayed = CountingUpGame.getTotalCardsPlayed();
        List<Card> unPlayedCards = new ArrayList<>(CountingUpGame.getTotalCardsInDeck());
        unPlayedCards.removeAll(totalCardsPlayed);
        unPlayedCards.removeAll(hand.getCardList()); // only get other players' hands
        return unPlayedCards;
    }



    private List<Integer> getOpponentsNumsOfCards(){
        List<Integer> numsOfCards = CountingUpGame.getNumsOfPlayerCards();
        numsOfCards.remove(Integer.valueOf(hand.getNumberOfCards()));
        return numsOfCards;
    }

    private boolean othersCanWinPotIfIPlaySelected(Card selectedCard){
        int othersValidCardsNumIfIPlaySelected = getOthersValidUnplayedCards(selectedCard).size();
//        System.out.println("| " +(selectedCard!=null)+  " | " + (othersValidCardsNumIfIPlaySelected > 0) + " | " + (getNumSameRankCard(selectedCard, hand.getCardList()) <= getNumSameRankCard(selectedCard, getOtherUnplayedCards())) + "|");
        if (selectedCard!=null
                && othersValidCardsNumIfIPlaySelected > 0
                && (getNumSameRankCard(selectedCard, hand.getCardList()) <= getNumSameRankCard(selectedCard, getOtherUnplayedCards())
                || getNumHigherRankSameSuitCard(selectedCard, getOtherUnplayedCards())>0)
        ){
             return true;
        }
        return false;
    }

}
