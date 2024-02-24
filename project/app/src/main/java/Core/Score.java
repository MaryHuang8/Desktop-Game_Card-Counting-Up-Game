package Core;

import ch.aplu.jcardgame.Card;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;

import java.awt.*;
import java.util.List;

public class Score {

    private static final int[] scores = new int[CountingUpGame.nbPlayers];
    private static Score instance = null;
    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            // new Location(650, 575)
            new Location(575, 575)
    };
    private final Actor[] scoreActors = {null, null, null, null};
    private final Font bigFont = new Font("Arial", Font.BOLD, 36);

    private Score(CountingUpGame game) {
        initScore(game);
        initScores();
    }

    public static Score getInstance(CountingUpGame game) {
        if (instance == null) {
            instance = new Score(game);
        }
        return instance;
    }

    public static int calculateScore(java.util.List<Card> cardsPlayed) {
        int totalScorePlayed = 0;
        for (Card card : cardsPlayed) {
            Rank rank = (Rank) card.getRank();
            totalScorePlayed += rank.getScoreCardValue();
        }
        return totalScorePlayed;
    }

    public static int[] getScores() {
        return scores;
    }

    private void initScore(CountingUpGame game) {
        for (int i = 0; i < CountingUpGame.nbPlayers; i++) {
            // scores[i] = 0;
            String text = "[" + scores[i] + "]";
            scoreActors[i] = new TextActor(text, Color.WHITE, game.bgColor, bigFont);
            game.addActor(scoreActors[i], scoreLocations[i]);
        }
    }

    private void initScores() {
        for (int i = 0; i < CountingUpGame.nbPlayers; i++) {
            scores[i] = 0;
        }
    }

    public void calculateScoreEndOfRound(int player, java.util.List<Card> cardsPlayed) {
        scores[player] += calculateScore(cardsPlayed);
    }

    public void calculateNegativeScoreEndOfGame(int player, List<Card> cardsInHand) {
        int totalScorePlayed = 0;
        for (Card card : cardsInHand) {
            Rank rank = (Rank) card.getRank();
            totalScorePlayed -= rank.getScoreCardValue();
        }
        scores[player] += totalScorePlayed;
    }

    public void updateScore(int player, CountingUpGame game) {
        game.removeActor(scoreActors[player]);
        int displayScore = scores[player] >= 0 ? scores[player] : 0;
        String text = "P" + player + "[" + displayScore + "]";
        scoreActors[player] = new TextActor(text, Color.WHITE, game.bgColor, bigFont);
        game.addActor(scoreActors[player], scoreLocations[player]);
    }

    public int getScore(int player) {
        return scores[player];
    }

    public boolean isZeroes() {
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] != 0) {
                return false;
            }
        }
        return true;

    }
}
