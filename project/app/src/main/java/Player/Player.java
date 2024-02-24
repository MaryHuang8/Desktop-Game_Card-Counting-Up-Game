package Player;

import Core.CountingUpGame;
import Core.Validator;
import ch.aplu.jcardgame.*;

import java.util.List;

public abstract class Player {
    Hand hand;
    boolean hideHand = false;

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    //related Hand methods
    protected void setHandTouchEnabled(boolean bool) {
        hand.setTouchEnabled(bool);
    }

    protected void addCardListenerToHand(CardListener cardListener) {
        hand.addCardListener(cardListener);
    }


    public void setHandView(CardGame game, RowLayout layout) {
        hand.setView(game, layout);
    }

    public void setHandTargetArea(TargetArea targetArea) {
        hand.setTargetArea(targetArea);
    }

    public void drawHand() {
        hand.draw();
    }

    /**
     * Method for setting up human computer interaction.
     * Default is none in the execution (for computer players).
     *
     * @param game
     */
    public void setHandCardListener(CountingUpGame game) {
        //none set as a default
    }


    //    public abstract playTurn(CountingUpGame game, List<Card> cardsPlayed, int nextPlayer);
    public abstract void playTurn(CountingUpGame game, List<Card> cardsPlayed, int nextPlayer);


    public Card getLowestCardOrSkip(Card prevCard) {

        List<Card> cardList = Validator.getValidCardList(prevCard, hand.getCardList());
        if (cardList.isEmpty()) {
            return null;
        }
        Card selectedCard = null;

        Card lowestValidCard = cardList.get(0);

        for (int i = 1; i < cardList.size(); i++) {

            if (Validator.rankGreaterThanOrEqual(cardList.get(i), lowestValidCard)) {
                lowestValidCard = cardList.get(i);
            }
        }
        selectedCard = lowestValidCard;

        return selectedCard;
    }

    public void hideCards() {
        for (Card card : hand.getCardList()) {
            card.getCardActor().show(1);

        }
    }

    public boolean isHideHand() {
        return hideHand;
    }

}
