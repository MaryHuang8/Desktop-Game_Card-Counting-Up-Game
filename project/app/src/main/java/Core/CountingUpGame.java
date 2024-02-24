package Core;// CountingUpGame.java

import Player.Player;
import Player.PlayerFactory;
import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGKeyListener;
import ch.aplu.jgamegrid.Location;

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.stream.Collectors;

public class CountingUpGame extends CardGame implements GGKeyListener {
    private static int seed;
    public static final Random random = new Random(seed);
    public static final int nbPlayers = 4;
    public static final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private static final Player[] players = new Player[nbPlayers];
    private static final List<Card> totalCardsPlayed = new ArrayList<>();
    private static final List<Card> totalCardsInDeck = new ArrayList<>();
    private static List<Card> cardsPlayed = new ArrayList<>();
    public final Location trickLocation = new Location(350, 350);
    private final Properties properties;
    private final List<List<String>> PLAYER_AUTO_MOVEMENTS = new ArrayList<>();
    private final String VERSION = "1.0";
    private final int TRICK_WIDTH = 40;
    private final Location TEXT_LOCATION = new Location(350, 450);
    //private Hand[] hands;
    private final Location HIDE_LOCATION = new Location(-500, -500);
    private final int[] AUTO_INDEX_HANDS = new int[nbPlayers];
    private final Score SCORE = Score.getInstance(this); //a central score controller
    private final InitialisationController INIT_CONTROLLER = new InitialisationController(this);
    private final EndGameController END_GAME_CONTROLLER = new EndGameController();
    private int thinkingTime = 2000;
    private int delayTime = 600;
    private boolean isWaitingForPass = false;
    private boolean passSelected = false;
    private boolean isAuto = false;
    private Card selected;
    public CountingUpGame(Properties properties) {
        super(700, 700, 30);
        this.properties = properties;
        isAuto = Boolean.parseBoolean(properties.getProperty("isAuto"));
        thinkingTime = Integer.parseInt(properties.getProperty("thinkingTime", "200"));
        delayTime = Integer.parseInt(properties.getProperty("delayTime", "100"));
        seed = Integer.parseInt(properties.getProperty("seed","30008"));
        //initialise players by properties position and type
        for (int i = 0; i < nbPlayers; i++) {
            String playerType = properties.getProperty("players." + i, "random");
            players[i] = PlayerFactory.getInstance().getPlayer(playerType);
        }
    }

    //TODO: Need to arrange code
    public static List<Card> getCardsPlayed() {
        return cardsPlayed;
    }

    public static List<Card> getTotalCardsPlayed() {
        return totalCardsPlayed;
    }

    public static List<Card> getTotalCardsInDeck() {
        return totalCardsInDeck;
    }

    public static int getLeastNumberofPlayerCards() {
        int minimumCards = deck.getNumberOfCards();
        for (Player player : players) {
            if (minimumCards > player.getHand().getNumberOfCards()) {
                minimumCards = player.getHand().getNumberOfCards();
            }
        }
        return minimumCards;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setStatus(String string) {
        setStatusText(string);
    }

    public void setWaitingForPass(boolean waitingForPass) {
        isWaitingForPass = waitingForPass;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public int getThinkingTime() {
        return thinkingTime;
    }

    public Card getSelected() {
        return selected;
    }

    public void setSelected(Card selected) {
        this.selected = selected;
    }

    public boolean isPassSelected() {
        return passSelected;
    }

//    private void addCardPlayedToLog(int player, Card selectedCard) {
//        if (selectedCard == null) {
//            logResult.append("P" + player + "-SKIP,");
//        } else {
//            Rank cardRank = (Rank) selectedCard.getRank();
//            Suit cardSuit = (Suit) selectedCard.getSuit();
//            logResult.append("P" + player + "-" + cardRank.getRankCardLog() + cardSuit.getSuitShortHand() + ",");
//        }
//    }
//
//    private void addRoundInfoToLog(int roundNumber) {
//        logResult.append("Round" + roundNumber + ":");
//    }
//
//    private void addEndOfRoundToLog() {
//        logResult.append("Score:");
//        int[] scores = score.getScores();
//        for (int i = 0; i < scores.length; i++) {
//            logResult.append(scores[i] + ",");
//        }
//        logResult.append("\n");
//    }
//
//    private void addEndOfGameToLog(List<Integer> winners) {
//        logResult.append("EndGame:");
//        int[] scores = score.getScores();
//        for (int i = 0; i < scores.length; i++) {
//            logResult.append(scores[i] + ",");
//        }
//        logResult.append("\n");
//        logResult.append("Winners:" + String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList())));
//    }

    public void setPassSelected(boolean passSelected) {
        this.passSelected = passSelected;
    }

    private int playerIndexWithAceClub() {
        int startPosition = 0;
        for (int i = 0; i < nbPlayers; i++) {
            Hand hand = players[i].getHand();
            List<Card> cards = hand.getCardsWithRank(Rank.ACE);
            if (cards.isEmpty()) {
                continue;
            }
            for (Card card : cards) {
                if (card.getSuit() == Suit.CLUBS) {
                    startPosition = i;
                    return startPosition;
                }
            }
        }

        return 0;
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (isWaitingForPass && keyEvent.getKeyChar() == '\n') {
            passSelected = true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(KeyEvent keyEvent) {
        return false;
    }

    private void playGame() {
        // End trump suit
        Hand playingArea = null;
        int winner = 0;
        int roundNumber = 1;
        for (int i = 0; i < nbPlayers; i++) SCORE.updateScore(i, this);
        boolean isContinue = true;
        int skipCount = 0;
        cardsPlayed = new ArrayList<>();
        playingArea = new Hand(deck);
        END_GAME_CONTROLLER.addRoundInfoToLog(roundNumber);

        int nextPlayer = playerIndexWithAceClub();
        while (isContinue) {
            selected = null;
            boolean finishedAuto = false;
            if (isAuto) {
                int nextPlayerAutoIndex = AUTO_INDEX_HANDS[nextPlayer];
                List<String> nextPlayerMovement = PLAYER_AUTO_MOVEMENTS.get(nextPlayer);
                String nextMovement = "";

                if (nextPlayerMovement.size() > nextPlayerAutoIndex) {
                    nextMovement = nextPlayerMovement.get(nextPlayerAutoIndex);
                    nextPlayerAutoIndex++;

                    AUTO_INDEX_HANDS[nextPlayer] = nextPlayerAutoIndex;
                    Hand nextHand = players[nextPlayer].getHand();

                    if (nextMovement.equals("SKIP")) {
                        setStatusText("Player " + nextPlayer + " skipping...");
                        delay(thinkingTime);
                        selected = null;
                    } else {
                        setStatusText("Player " + nextPlayer + " thinking...");
                        delay(thinkingTime);

                        selected = INIT_CONTROLLER.getCardFromList(nextHand.getCardList(), nextMovement);

                    }
                } else {
                    finishedAuto = true;
                }
            }

            if (!isAuto || finishedAuto) {
                // play ace of clubs in the 1st turn
                if (cardsPlayed.isEmpty() && SCORE.isZeroes()) {
                    setStatusText("Player " + nextPlayer + " thinking...");
                    delay(thinkingTime);
                    selected = INIT_CONTROLLER.getCardFromList(players[nextPlayer].getHand().getCardList(), "0C");

                } else {
//                    System.out.println("PLAYER: " + nextPlayer);
                    players[nextPlayer].playTurn(this, cardsPlayed, nextPlayer); //sets selected according to player playing logic
                }
            }

            // Follow with selected card

            playingArea.setView(this, new RowLayout(trickLocation, (playingArea.getNumberOfCards() + 2) * TRICK_WIDTH));
            playingArea.draw();
            END_GAME_CONTROLLER.addCardPlayedToLog(nextPlayer, selected);
            if (selected != null) {
                if (Validator.isValidMove(selected, cardsPlayed)) {
                    skipCount = 0;
                    cardsPlayed.add(selected);
                    totalCardsPlayed.add(selected);
                    selected.setVerso(false);
                    selected.transfer(playingArea, true);

                    // set cards faced down for non-human players after resetting cards
                    for (int i = 0; i < nbPlayers; i++) {
                        if (players[i].isHideHand()) {
                            players[i].hideCards();
                        }

                    }

                    delay(delayTime);
                } else {
                    // The selected card is not valid, prompt the player to reselect
                    setStatusText("Player " + nextPlayer + " invalid move. Please select another card.");
                    delay(thinkingTime);
                    continue;
                }
            } else {
                skipCount++;
            }

            if (skipCount == nbPlayers - 1) {
                playingArea.setView(this, new RowLayout(HIDE_LOCATION, 0));
                playingArea.draw();
                winner = (nextPlayer + 1) % nbPlayers;
                skipCount = 0;
                SCORE.calculateScoreEndOfRound(winner, cardsPlayed);
                SCORE.updateScore(winner, this);
                END_GAME_CONTROLLER.addEndOfRoundToLog();
                roundNumber++;
                END_GAME_CONTROLLER.addRoundInfoToLog(roundNumber);
                cardsPlayed = new ArrayList<>();
                delay(delayTime);
                playingArea = new Hand(deck);
            }

            isContinue = players[0].getHand().getNumberOfCards() > 0 &&
                    players[1].getHand().getNumberOfCards() > 0 &&
                    players[2].getHand().getNumberOfCards() > 0 && players[3].getHand().getNumberOfCards() > 0;
            if (!isContinue) {
                winner = nextPlayer;
                SCORE.calculateScoreEndOfRound(winner, cardsPlayed);
                END_GAME_CONTROLLER.addEndOfRoundToLog();
            } else {
                nextPlayer = (nextPlayer + 1) % nbPlayers;
            }
            delay(delayTime);
        }

        for (int i = 0; i < nbPlayers; i++) {
            SCORE.calculateNegativeScoreEndOfGame(i, players[i].getHand().getCardList());
            SCORE.updateScore(i, this);
        }
    }

    private void setupPlayerAutoMovements() {
        String player0AutoMovement = properties.getProperty("players.0.cardsPlayed");
        String player1AutoMovement = properties.getProperty("players.1.cardsPlayed");
        String player2AutoMovement = properties.getProperty("players.2.cardsPlayed");
        String player3AutoMovement = properties.getProperty("players.3.cardsPlayed");

        String[] playerMovements = new String[]{"", "", "", ""};
        if (player0AutoMovement != null) {
            playerMovements[0] = player0AutoMovement;
        }

        if (player1AutoMovement != null) {
            playerMovements[1] = player1AutoMovement;
        }

        if (player2AutoMovement != null) {
            playerMovements[2] = player2AutoMovement;
        }

        if (player3AutoMovement != null) {
            playerMovements[3] = player3AutoMovement;
        }

        for (int i = 0; i < playerMovements.length; i++) {
            String movementString = playerMovements[i];
            List<String> movements = Arrays.asList(movementString.split(","));
            PLAYER_AUTO_MOVEMENTS.add(movements);
        }
    }

    public String runApp() {
        setTitle("CountingUpGame (V" + VERSION + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        addKeyListener(this);
        setupPlayerAutoMovements();
        INIT_CONTROLLER.initGame(players);
        playGame();

        for (int i = 0; i < nbPlayers; i++) SCORE.updateScore(i, this);
        int maxScore = 0;
        int[] scores = Score.getScores();
        for (int i = 0; i < nbPlayers; i++) if (scores[i] > maxScore) maxScore = scores[i];
        List<Integer> winners = new ArrayList<Integer>();
        for (int i = 0; i < nbPlayers; i++) if (scores[i] == maxScore) winners.add(i);
        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " +
                    winners.iterator().next();
        } else {
            winText = "Game Over. Drawn winners are players: " +
                    String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        addActor(new Actor("sprites/gameover.gif"), TEXT_LOCATION);
        setStatusText(winText);
        refresh();
        END_GAME_CONTROLLER.addEndOfGameToLog(winners);

        return END_GAME_CONTROLLER.logResult();
    }
    public static List<Integer> getNumsOfPlayerCards(){
        List<Integer> numsOfCards = new ArrayList<>();
        for (Player player :players){
            numsOfCards.add(player.getHand().getNumberOfCards());
        }
        return numsOfCards;
    }
}
