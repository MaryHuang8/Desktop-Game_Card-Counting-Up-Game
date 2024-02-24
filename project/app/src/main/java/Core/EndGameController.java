package Core;

import ch.aplu.jcardgame.Card;

import java.util.List;
import java.util.stream.Collectors;

public class EndGameController {
    private final StringBuilder logResult = new StringBuilder();

    public String logResult() {
        return logResult.toString();
    }

    protected void addCardPlayedToLog(int player, Card selectedCard) {
        if (selectedCard == null) {
            logResult.append("P" + player + "-SKIP,");
        } else {
            Rank cardRank = (Rank) selectedCard.getRank();
            Suit cardSuit = (Suit) selectedCard.getSuit();
            logResult.append("P" + player + "-" + cardRank.getRankCardLog() + cardSuit.getSuitShortHand() + ",");
        }
    }

    protected void addRoundInfoToLog(int roundNumber) {
        logResult.append("Round" + roundNumber + ":");
    }

    protected void addEndOfRoundToLog() {
        logResult.append("Score:");
        int[] scores = Score.getScores();
        for (int i = 0; i < scores.length; i++) {
            logResult.append(scores[i] + ",");
        }
        logResult.append("\n");
    }

    protected void addEndOfGameToLog(List<Integer> winners) {
        logResult.append("EndGame:");
        int[] scores = Score.getScores();
        for (int i = 0; i < scores.length; i++) {
            logResult.append(scores[i] + ",");
        }
        logResult.append("\n");
        logResult.append("Winners:" + String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList())));
    }
}
