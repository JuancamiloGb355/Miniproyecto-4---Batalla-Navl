package edu.univalle.battleship.designpatterns.strategy;

import edu.univalle.battleship.model.Board;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Implements a hunting and targeting shooting strategy for the machine player.
 * <p>
 * The strategy initially fires randomly (hunt mode) until a hit is made,
 * then switches to target mode to try adjacent cells to sink the ship.
 */
public class HuntTargetShootingStrategy implements IShootingStrategy, Serializable {

    private int lastRow = -1;
    private int lastCol = -1;

    private boolean targetMode = false;
    private final Deque<int[]> targets = new ArrayDeque<>();

    // Reuses the random hunting strategy for hunt mode
    private final RandomShootingStrategy huntStrategy = new RandomShootingStrategy();

    /**
     * Selects the next target for shooting.
     * <p>
     * In target mode, it chooses from adjacent cells of previously hit ships.
     * In hunt mode, it selects a random cell.
     *
     * @param board the board to select a target from
     * @return an array {row, column} representing the chosen target
     */
    @Override
    public int[] selectTarget(Board board) {
        if (targetMode && !targets.isEmpty()) {
            return targets.poll();
        }
        targetMode = false;
        return huntStrategy.selectTarget(board);
    }

    /**
     * Fires a shot at the board using the strategy.
     * <p>
     * Updates target mode and adds adjacent cells if a hit is made.
     *
     * @param board the board to shoot at
     * @return the result of the shot, e.g., "hit", "miss", "sunk:ShipName"
     */
    @Override
    public String shoot(Board board) {
        int[] pos = selectTarget(board);
        int row = pos[0];
        int col = pos[1];

        lastRow = row;
        lastCol = col;

        String result = board.receiveShot(row, col);

        if (result.equals("hit")) {
            targetMode = true;
            addAdjacentTargets(board, row, col);
        }

        if (result.startsWith("sunk")) {
            targetMode = false;
            targets.clear();
        }

        return result;
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

    /**
     * Adds adjacent cells of a hit cell to the target queue.
     * <p>
     * Only adds cells that are within the board and not already hit.
     *
     * @param board the board containing the cells
     * @param r     row of the hit
     * @param c     column of the hit
     */
    private void addAdjacentTargets(Board board, int r, int c) {
        int[][] cells = board.getCells();

        int[][] dirs = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };

        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];

            if (nr >= 0 && nr < Board.SIZE &&
                    nc >= 0 && nc < Board.SIZE &&
                    (cells[nr][nc] == 0 || cells[nr][nc] == 1)) {
                targets.add(new int[]{nr, nc});
            }
        }
    }
}
