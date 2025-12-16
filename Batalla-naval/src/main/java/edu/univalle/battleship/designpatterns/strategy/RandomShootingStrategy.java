package edu.univalle.battleship.designpatterns.strategy;

import edu.univalle.battleship.model.Board;
import java.util.Random;

/**
 * Implements a random shooting strategy for the machine player.
 * <p>
 * The strategy selects a random cell on the board that has not been targeted yet.
 * It keeps track of the last shot coordinates.
 */
public class RandomShootingStrategy implements IShootingStrategy {

    /** The row of the last shot fired. */
    private int lastRow;

    /** The column of the last shot fired. */
    private int lastCol;

    /** Random generator for selecting targets. */
    private final Random random = new Random();

    /**
     * Selects a target cell randomly from the board.
     * Only selects cells that are empty or contain a ship.
     *
     * @param board the board to select a target from
     * @return an array {row, column} representing the selected target
     */
    @Override
    public int[] selectTarget(Board board) {
        int r, c;
        int[][] cells = board.getCells();

        do {
            r = random.nextInt(Board.SIZE);
            c = random.nextInt(Board.SIZE);
        } while (cells[r][c] != 0 && cells[r][c] != 1);

        return new int[]{r, c};
    }

    /**
     * Fires a shot at the board using the selected target.
     * Updates the last shot coordinates.
     *
     * @param board the board to shoot at
     * @return the result of the shot ("hit", "miss", "sunk:ShipName", or "already")
     */
    @Override
    public String shoot(Board board) {
        int[] pos = selectTarget(board);
        lastRow = pos[0];
        lastCol = pos[1];
        return board.receiveShot(lastRow, lastCol);
    }

    /**
     * Returns the coordinates of the last shot fired.
     *
     * @return an array {row, column} of the last shot
     */
    @Override
    public int[] getLastShotCoordinates() {
        return new int[]{lastRow, lastCol};
    }
}
