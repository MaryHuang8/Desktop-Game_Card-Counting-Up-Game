package Player;


public class PlayerFactory {
    private static PlayerFactory _instance = null;
    private Player player = null;

    private PlayerFactory() {

    }

    public static PlayerFactory getInstance() {
        if (_instance == null) {
            _instance = new PlayerFactory();
        }
        return _instance;
    }

    public Player getPlayer(String playerType) {
        switch (playerType) {
            case "human" -> player = new HumanPlayer();
            case "random" -> player = new RandomPlayer();
            case "basic" -> player = new BasicPlayer();
            case "clever" -> player = new CleverPlayer();
            default -> System.out.println("error matching player type: " + playerType);
        }
        return player;
    }
}
