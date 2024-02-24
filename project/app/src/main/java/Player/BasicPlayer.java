package Player;

import ch.aplu.jcardgame.Card;

public class BasicPlayer extends ComputerPlayer {
    @Override
    public Card getCardOrSkip(Card prevCard) {
        return getLowestCardOrSkip(prevCard);
    }

}
