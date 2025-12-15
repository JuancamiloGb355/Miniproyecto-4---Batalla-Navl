package edu.univalle.battleship.model;

import edu.univalle.battleship.controller.PositionController;
import javafx.scene.layout.GridPane;

import java.io.Serializable;

public class GameManager implements Serializable {

    private static final GameManager INSTANCE = new GameManager();

    private Player human;
    private MachinePlayer machine;

    private boolean isPlayerTurn = true; // true = jugador, false = máquina

    private GameManager() {}

    private GridPane playerBoardGrid;

    private boolean gameOver = false;

    private transient PositionController positionController;

    public void setPositionController(PositionController pc) {
        this.positionController = pc;
    }

    public PositionController getPositionController() {
        return positionController;
    }


    public boolean isGameOver() {
        return gameOver;
    }

    public void checkGameOver() {
        if (isHumanDefeated() || isMachineDefeated()) {
            gameOver = true;
        }
    }

    public void resetGame() {
        this.human = null;
        this.machine = null;
        this.isPlayerTurn = true;
    }


    public GridPane getPlayerBoardGrid() {
        return playerBoardGrid;
    }

    public void setPlayerBoardGrid(GridPane grid) {
        this.playerBoardGrid = grid;
    }

    public static GameManager getInstance() {
        return INSTANCE;
    }

    public void startNewGame(Player humanPlayer) {
        this.human = humanPlayer;

        this.machine = new MachinePlayer();
        this.machine.placeFleetAutomatically();

        this.isPlayerTurn = true;
    }

    public Player getHuman() {
        return human;
    }

    public MachinePlayer getMachine() {
        return machine;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public void setPlayerTurn(boolean turn) {
        this.isPlayerTurn = turn;
    }

    public boolean isHumanDefeated() {
        return human.getBoard().getSunkenShips() == 10;
    }

    public boolean isMachineDefeated() {
        return machine.getBoard().getSunkenShips() == 10;
    }
}