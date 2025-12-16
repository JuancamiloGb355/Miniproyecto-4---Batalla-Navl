package edu.univalle.battleship.model;

import edu.univalle.battleship.controller.PositionController;
import javafx.scene.layout.GridPane;
import java.io.Serializable;

/**
 * Central game coordinator that manages players, boards and global game state.
 * <p>
 * This class follows the Singleton pattern to ensure that only one game
 * instance exists during execution.
 * </p>
 */
public class GameManager implements Serializable {

    // ----------------------------
    // SINGLETON
    // ----------------------------

    /**
     * Single instance of the GameManager.
     */
    private static final GameManager INSTANCE = new GameManager();

    /**
     * Private constructor to prevent external instantiation.
     */
    private GameManager() {}

    /**
     * Returns the single instance of the GameManager.
     *
     * @return the unique GameManager instance
     */
    public static GameManager getInstance() {
        return INSTANCE;
    }

    // ----------------------------
    // PLAYERS AND BOARDS
    // ----------------------------

    /**
     * Human player instance.
     */
    private Player human;

    /**
     * Machine (AI) player instance.
     */
    private MachinePlayer machine;

    /**
     * GridPane representing the human player's board in the UI.
     */
    private GridPane playerBoardGrid;

    /**
     * Indicates whose turn it is.
     * True = human player's turn, false = machine's turn.
     */
    private boolean isPlayerTurn = true;

    /**
     * Indicates whether the game has ended.
     */
    private boolean gameOver = false;

    /**
     * Reference to the position controller.
     * Marked as transient to avoid serialization issues.
     */
    private transient PositionController positionController;

    // ----------------------------
    // GETTERS AND SETTERS
    // ----------------------------

    /**
     * Returns the human player.
     *
     * @return the human player
     */
    public Player getHuman() {
        return human;
    }

    /**
     * Sets the human player.
     *
     * @param human the human player
     */
    public void setHuman(Player human) {
        this.human = human;
    }

    /**
     * Returns the machine player.
     *
     * @return the machine player
     */
    public MachinePlayer getMachine() {
        return machine;
    }

    /**
     * Sets the machine player.
     *
     * @param machine the machine player
     */
    public void setMachine(MachinePlayer machine) {
        this.machine = machine;
    }

    /**
     * Updates whose turn it is.
     *
     * @param turn true for human turn, false for machine turn
     */
    public void setPlayerTurn(boolean turn) {
        this.isPlayerTurn = turn;
    }

    /**
     * Returns the GridPane of the human player's board.
     *
     * @return the player's board grid
     */
    public GridPane getPlayerBoardGrid() {
        return playerBoardGrid;
    }

    /**
     * Sets the GridPane of the human player's board.
     *
     * @param grid the player's board grid
     */
    public void setPlayerBoardGrid(GridPane grid) {
        this.playerBoardGrid = grid;
    }

    /**
     * Sets the position controller reference.
     *
     * @param pc the position controller
     */
    public void setPositionController(PositionController pc) {
        this.positionController = pc;
    }

    // ----------------------------
    // GAME METHODS
    // ----------------------------

    /**
     * Initializes a new game with a human player and a newly created machine player.
     * The machine fleet is placed automatically.
     *
     * @param humanPlayer the human player
     */
    public void startNewGame(Player humanPlayer) {
        this.human = humanPlayer;

        this.machine = new MachinePlayer();
        this.machine.placeFleetAutomatically();

        this.isPlayerTurn = true;
        this.gameOver = false;
    }

    /**
     * Resets the entire game state, clearing players and UI references.
     */
    public void resetGame() {
        this.human = null;
        this.machine = null;
        this.isPlayerTurn = true;
        this.playerBoardGrid = null;
        this.gameOver = false;
        this.positionController = null;
    }

    /**
     * Checks whether the human player has been defeated.
     *
     * @return true if all human ships are sunk, false otherwise
     */
    public boolean isHumanDefeated() {
        if (human == null) return false;

        for (Ship ship : human.getFleet()) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the machine player has been defeated.
     *
     * @return true if all machine ships are sunk, false otherwise
     */
    public boolean isMachineDefeated() {
        if (machine == null) return false;

        for (Ship ship : machine.getFleet()) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }
}
