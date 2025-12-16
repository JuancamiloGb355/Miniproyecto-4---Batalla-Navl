package edu.univalle.battleship.model;

import edu.univalle.battleship.controller.PositionController;
import javafx.scene.layout.GridPane;
import java.io.Serializable;
import java.util.List;

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

    public boolean isHumanDefeated() {
        if (human == null) return false;

        for (Ship ship : human.getFleet()) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    public boolean isMachineDefeated() {
        if (machine == null) return false;

        for (Ship ship : machine.getFleet()) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }


    // Método auxiliar para contar barcos hundidos
    private int countSunkShips(int[][] board, List<Ship> fleet) {
        int count = 0;
        for (Ship ship : fleet) {
            boolean sunk = true;
            int[][] positions = ship.getPositions();
            for (int[] pos : positions) {
                int r = pos[0];
                int c = pos[1];
                if (board[r][c] != 3) { // 3 = sunk
                    sunk = false;
                    break;
                }
            }
            if (sunk) count++;
        }
        return count;
    }

    public void endGame() {
    }
}
