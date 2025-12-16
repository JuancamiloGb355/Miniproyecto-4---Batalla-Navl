package edu.univalle.battleship.model;

import edu.univalle.battleship.controller.PositionController;
import javafx.scene.layout.GridPane;
import java.io.Serializable;

/**
 * Singleton class that manages the state of the game.
 * <p>
 * It holds references to the human player, machine player, and the game board.
 * Also tracks turns and whether the game is over.
 */
public class GameManager implements Serializable {

    // ----------------------------
    // SINGLETON INSTANCE
    // ----------------------------
    private static final GameManager INSTANCE = new GameManager();

    private GameManager() {}

    /**
     * Returns the singleton instance of the GameManager.
     *
     * @return the single instance
     */
    public static GameManager getInstance() {
        return INSTANCE;
    }

    // ----------------------------
    // GAME STATE VARIABLES
    // ----------------------------
    private Player human;
    private MachinePlayer machine;

    private GridPane playerBoardGrid;

    private boolean isPlayerTurn = true; // true = human's turn, false = machine's turn
    private boolean gameOver = false;

    private transient PositionController positionController;

    // ----------------------------
    // GETTERS AND SETTERS
    // ----------------------------

    /** Returns the human player. */
    public Player getHuman() { return human; }

    /** Sets the human player. */
    public void setHuman(Player human) { this.human = human; }

    /** Returns the machine player. */
    public MachinePlayer getMachine() { return machine; }

    /** Sets the machine player. */
    public void setMachine(MachinePlayer machine) { this.machine = machine; }

    /** Returns true if it is the human player's turn. */
    public boolean isPlayerTurn() { return isPlayerTurn; }

    /** Sets whose turn it is. */
    public void setPlayerTurn(boolean turn) { this.isPlayerTurn = turn; }

    /** Returns the GridPane used to display the player's board. */
    public GridPane getPlayerBoardGrid() { return playerBoardGrid; }

    /** Sets the GridPane for the player's board. */
    public void setPlayerBoardGrid(GridPane grid) { this.playerBoardGrid = grid; }

    /** Sets the PositionController for UI interactions. */
    public void setPositionController(PositionController pc) { this.positionController = pc; }

    // ----------------------------
    // GAME METHODS
    // ----------------------------

    /**
     * Starts a new game with a given human player.
     * <p>
     * Automatically creates a machine player and places its fleet.
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
     * Resets the game state.
     * <p>
     * Clears players, board, turn, and position controller.
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
     * Checks if all ships of the human player have been sunk.
     *
     * @return true if the human player has been defeated
     */
    public boolean isHumanDefeated() {
        if (human == null) return false;
        for (Ship ship : human.getFleet()) {
            if (!ship.isSunk()) return false;
        }
        return true;
    }

    /**
     * Checks if all ships of the machine player have been sunk.
     *
     * @return true if the machine player has been defeated
     */
    public boolean isMachineDefeated() {
        if (machine == null) return false;
        for (Ship ship : machine.getFleet()) {
            if (!ship.isSunk()) return false;
        }
        return true;
    }
}
