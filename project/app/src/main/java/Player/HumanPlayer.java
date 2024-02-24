package Player;

import Core.CountingUpGame;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.CardListener;
import ch.aplu.jgamegrid.GameGrid;

import java.util.List;

public class HumanPlayer extends Player {

    public HumanPlayer() {
        super();
        super.hideHand = false;
    }

    /**
     * Set up human computer interaction for human plays card.
     *
     * @param game
     */
    @Override
    public void setHandCardListener(CountingUpGame game) {

        // Set up human player for interaction
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) {
                game.setSelected(card);
                setHandTouchEnabled(false);
            }
        };
        addCardListenerToHand(cardListener);
    }

    @Override
    public void playTurn(CountingUpGame game, List<Card> cardsPlayed, int nextPlayer) {
        setHandTouchEnabled(true);
        game.setWaitingForPass(true);
        game.setPassSelected(false);
        game.setStatus("Player 0 double-click on card to follow or press Enter to pass");
        while (null == game.getSelected() && !game.isPassSelected()) GameGrid.delay(game.getDelayTime());
        game.setWaitingForPass(false);
    }
}
