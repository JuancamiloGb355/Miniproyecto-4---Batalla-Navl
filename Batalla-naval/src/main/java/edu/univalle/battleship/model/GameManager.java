package edu.univalle.battleship.model;

import edu.univalle.battleship.controller.PositionController;
import javafx.scene.layout.GridPane;
import java.io.Serializable;

public class GameManager implements Serializable {

    // ----------------------------
    // SINGLETON
    // ----------------------------
    private static final GameManager INSTANCE = new GameManager();

    private GameManager() {}

    public static GameManager getInstance() {
        return INSTANCE;
    }

    // ----------------------------
    // JUGADORES Y TABLERO
    // ----------------------------
    private Player human;
    private MachinePlayer machine;

    private GridPane playerBoardGrid;

    private boolean isPlayerTurn = true; // true = jugador, false = máquina
    private boolean gameOver = false;

    private transient PositionController positionController;

    // ----------------------------
    // GETTERS Y SETTERS
    // ----------------------------
    public Player getHuman() { return human; }
    public void setHuman(Player human) { this.human = human; }

    public MachinePlayer getMachine() { return machine; }
    public void setMachine(MachinePlayer machine) { this.machine = machine; }

    public boolean isPlayerTurn() { return isPlayerTurn; }
    public void setPlayerTurn(boolean turn) { this.isPlayerTurn = turn; }

    public GridPane getPlayerBoardGrid() { return playerBoardGrid; }
    public void setPlayerBoardGrid(GridPane grid) { this.playerBoardGrid = grid; }

    public PositionController getPositionController() { return positionController; }
    public void setPositionController(PositionController pc) { this.positionController = pc; }

    public boolean isGameOver() { return gameOver; }

    // ----------------------------
    // MÉTODOS DE JUEGO
    // ----------------------------
    public void startNewGame(Player humanPlayer) {
        this.human = humanPlayer;

        this.machine = new MachinePlayer();
        this.machine.placeFleetAutomatically();

        this.isPlayerTurn = true;
        this.gameOver = false;
    }

    public void resetGame() {
        this.human = null;
        this.machine = null;
        this.isPlayerTurn = true;
        this.playerBoardGrid = null;
        this.gameOver = false;
        this.positionController = null;
    }

    public void checkGameOver() {
        if (isHumanDefeated() || isMachineDefeated()) {
            gameOver = true;
        }
    }

    public boolean isHumanDefeated() {
        return human != null && human.getBoard().getSunkenShips() == 10;
    }

    public boolean isMachineDefeated() {
        return machine != null && machine.getBoard().getSunkenShips() == 10;
    }

}
