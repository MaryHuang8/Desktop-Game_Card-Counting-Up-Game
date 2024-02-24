package Player;

import Core.CountingUpGame;
import Core.Validator;
import ch.aplu.jcardgame.Card;

import java.util.List;

public class RandomPlayer extends ComputerPlayer {

    @Override
    public Card getCardOrSkip(Card prevCard) {
        return getRandomCardOrSkip(prevCard);
    }

    public Card getRandomCardOrSkip(Card prevCard) {

        List<Card> cardList = Validator.getValidCardList(prevCard, hand.getCardList());
        if (cardList.isEmpty()) {
            return null;
        }
        Card selectedCard = null;

        int randomIndex = CountingUpGame.random.nextInt(cardList.size());
        selectedCard = cardList.get(randomIndex);
        return selectedCard;
    }
}
