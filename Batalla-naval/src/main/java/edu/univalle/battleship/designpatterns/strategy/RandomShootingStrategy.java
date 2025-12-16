package edu.univalle.battleship.designpatterns.strategy;

import edu.univalle.battleship.model.Board;
import java.util.Random;

/**
 * Shooting strategy that selects targets randomly.
 * <p>
 * This strategy represents the simplest form of AI behavior,
 * where each shot is chosen randomly from the available
 * unshot cells on the board.
 */
public class RandomShootingStrategy implements IShootingStrategy {

    /**
     * Row index of the last shot fired.
     */
    private int lastRow;

    /**
     * Column index of the last shot fired.
     */
    private int lastCol;

    /**
     * Random number generator used to select target coordinates.
     */
    private final Random random = new Random();

    /**
     * Selects a random valid target on the board.
     * <p>
     * A valid target is a cell that has not been previously shot,
     * represented by a value of {@code 0} or {@code 1}.
     *
     * @param board the game board to analyze
     * @return an array containing the row and column of the selected target
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
     * Executes a shot on the board using a randomly selected target.
     *
     * @param board the game board where the shot will be executed
     * @return a string describing the result of the shot
     */
    @Override
    public String shoot(Board board) {
        int[] pos = selectTarget(board);
        lastRow = pos[0];
        lastCol = pos[1];
        return board.receiveShot(lastRow, lastCol);
    }

    /**
     * Returns the coordinates of the last shot fired by this strategy.
     *
     * @return an array containing the row and column of the last shot
     */
    @Override
    public int[] getLastShotCoordinates() {
        return new int[]{lastRow, lastCol};
    }
}
