package Core;

import Player.Player;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jcardgame.RowLayout;
import ch.aplu.jcardgame.TargetArea;
import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.List;


public class InitialisationController {

    private final CountingUpGame game;
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };
    private final int HAND_WIDTH = 400;

    public InitialisationController(CountingUpGame game) {
        this.game = game;
    }

    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list) {
        int x = CountingUpGame.random.nextInt(list.size());
        return list.get(x);
    }

    public void initGame(Player[] players) {
        int nbPlayers = CountingUpGame.nbPlayers;
        Hand[] hands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = new Hand(CountingUpGame.deck);
        }
        dealingOut(hands);
        for (int i = 0; i < nbPlayers; i++) {
            Validator.sort(hands[i]);
        }

        for (int i = 0; i < nbPlayers; i++) {
            players[i].setHand(hands[i]); //set new player hand
            players[i].setHandCardListener(game);// Set up human player for interaction
            CountingUpGame.getTotalCardsInDeck().addAll(hands[i].getCardList());

        }

        // Set up human player for interaction
//        CardListener cardListener = new CardAdapter()  // Human Player plays card
//        {
//            public void leftDoubleClicked(Card card) {
//                selected = card;
//                players[0].setHandTouchEnabled(false); //hand[0].setTouchEnabled(false)
//            }
//        };
//        players[0].addCardListenerToHand(cardListener);
        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], HAND_WIDTH);
            layouts[i].setRotationAngle(90 * i);
            // layouts[i].setStepDelay(10);
            players[i].setHandView(game, layouts[i]); //hands[i].setView(this, layouts[i]);
            players[i].setHandTargetArea(new TargetArea(game.trickLocation)); //hands[i].setTargetArea(new TargetArea(trickLocation));
            players[i].drawHand(); //hands[i].draw();
        }

        // initialize non-human player cards faced down
        for (int i = 0; i < nbPlayers; i++) {
            if (players[i].isHideHand()) {
                players[i].hideCards();
            }

        }

    }

    private void dealingOut(Hand[] hands) {
        Hand pack = CountingUpGame.deck.toHand(false);

        for (int i = 0; i < CountingUpGame.nbPlayers; i++) {
            String initialCardsKey = "players." + i + ".initialcards";
            String initialCardsValue = game.getProperties().getProperty(initialCardsKey);
            if (initialCardsValue == null) {
                continue;
            }
            String[] initialCards = initialCardsValue.split(",");
            for (String initialCard : initialCards) {
                if (initialCard.length() <= 1) {
                    continue;
                }
                Card card = getCardFromList(pack.getCardList(), initialCard);
                if (card != null) {
                    card.removeFromHand(false);
                    hands[i].insert(card, false);
                }
            }
        }

        for (int i = 0; i < CountingUpGame.nbPlayers; i++) {
            int cardsToDealt = 13 - hands[i].getNumberOfCards();
            for (int j = 0; j < cardsToDealt; j++) {
                if (pack.isEmpty()) return;
                Card dealt = randomCard(pack.getCardList());
                dealt.removeFromHand(false);
                hands[i].insert(dealt, false);
            }
        }
    }

    public static Card getCardFromList(List<Card> cards, String cardName) {
        Rank cardRank = getRankFromString(cardName);
        Suit cardSuit = getSuitFromString(cardName);
        for (Card card : cards) {
            if (card.getSuit() == cardSuit
                    && card.getRank() == cardRank) {
                return card;
            }
        }

        return null;
    }

    private static Rank getRankFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        int rankValue = Integer.parseInt(rankString);

        for (Rank rank : Rank.values()) {
            if (rank.getRankCardValue() == rankValue) {
                return rank;
            }
        }

        return Rank.ACE;
    }

    private static Suit getSuitFromString(String cardName) {
        String suitString = cardName.substring(cardName.length() - 1);

        for (Suit suit : Suit.values()) {
            if (suit.getSuitShortHand().equals(suitString)) {
                return suit;
            }
        }
        return Suit.CLUBS;
    }
}
