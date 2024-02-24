package Core;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Validator {
    public static boolean checkValidation(Card card1, Card card2) {
        Rank rank1 = Rank.valueOf(card1.getRank().toString());
        Rank rank2 = Rank.valueOf(card2.getRank().toString());
        Suit suit1 = Suit.valueOf(card1.getSuit().toString());
        Suit suit2 = Suit.valueOf(card2.getSuit().toString());

        boolean sameRank = rank1 == rank2;
        boolean sameSuit = suit1 == suit2;
        boolean rankHigher = rankGreaterThanOrEqual(card1, card2);

        return (sameRank && !sameSuit) || (!sameRank && sameSuit && rankHigher);
    }

    /**
     * Find cards on hand that are greater than the prev card's rank
     *
     * @param prevCard, cardList
     * @return validCardList
     */
    public static List<Card> getValidCardList(Card prevCard, List<Card> cardList) {

        if (prevCard == null) {
            return cardList;
        }
        List<Card> validCardList = new ArrayList<>();
        for (int i = 0; i < cardList.size(); i++) {
            if (checkValidation(prevCard, cardList.get(i))) {
                validCardList.add(cardList.get(i));
            }

        }
        return validCardList;
    }

    //TODO: Reduction
    public static boolean rankGreaterThanOrEqual(Card card1, Card card2) {
        Rank rank1 = Rank.valueOf(card1.getRank().toString());
        Rank rank2 = Rank.valueOf(card2.getRank().toString());

        return rank1.getRankCardValue() <= rank2.getRankCardValue();
    }

    public static boolean rankEqual(Card card1, Card card2) {
        Rank rank1 = Rank.valueOf(card1.getRank().toString());
        Rank rank2 = Rank.valueOf(card2.getRank().toString());

        return rank1.getRankCardValue() == rank2.getRankCardValue();
    }

    public static boolean suitEqual(Card card1, Card card2){
        Suit suit1 = Suit.valueOf(card1.getSuit().toString());
        Suit suit2 = Suit.valueOf(card2.getSuit().toString());

        return suit1.getSuitShortHand() == suit2.getSuitShortHand();
    }

    public static void sort(Hand hand) {
        ArrayList<Card> cardList = hand.getCardList();
        if (!cardList.isEmpty()) {
            Comparator<Card> rankComparator = Comparator.comparingInt(card -> Rank.valueOf(card.getRank().toString()).getRankCardValue());
            Collections.sort(cardList, rankComparator);
        }
    }

    public static boolean isValidMove(Card selected, List<Card> cardsPlayed) {
        if (cardsPlayed.isEmpty()) {
            // If it's the first move of the round, any card is valid
            return true;
        } else {
            Card lastPlayed = cardsPlayed.get(cardsPlayed.size() - 1);
            return checkValidation(lastPlayed, selected);
        }
    }
}
