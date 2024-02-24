package Player;

import Core.CountingUpGame;
import ch.aplu.jcardgame.Card;
import ch.aplu.jgamegrid.GameGrid;

import java.util.List;

public abstract class ComputerPlayer extends Player {
    public void playTurn(CountingUpGame game, List<Card> cardsPlayed, int nextPlayer) {
        game.setStatusText("Player " + nextPlayer + " thinking...");
        GameGrid.delay(game.getThinkingTime());
        Card prevCard = null;

        if (!cardsPlayed.isEmpty()) {
            prevCard = cardsPlayed.get(cardsPlayed.size() - 1);
        }
        game.setSelected(getCardOrSkip(prevCard));
        if (game.getSelected() == null) {
            game.setStatusText("Player " + nextPlayer + " skipping...");
            GameGrid.delay(game.getThinkingTime());
        }
    }

    public abstract Card getCardOrSkip(Card prevCard);
}
