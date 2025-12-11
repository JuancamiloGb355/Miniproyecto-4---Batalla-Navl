package edu.univalle.battleship.model;

import javafx.scene.layout.GridPane;

public class GameManager {

    private static final GameManager INSTANCE = new GameManager();

    private Player human;
    private MachinePlayer machine;

    private boolean isPlayerTurn = true; // true = jugador, false = máquina

    private GameManager() {}

    private GridPane playerBoardGrid;

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
}