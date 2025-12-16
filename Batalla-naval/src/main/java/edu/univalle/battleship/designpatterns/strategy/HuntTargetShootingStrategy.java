package edu.univalle.battleship.designpatterns.strategy;

import edu.univalle.battleship.model.Board;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Shooting strategy that combines a random "hunt" phase with a focused
 * "target" phase.
 * <p>
 * The strategy works as follows:
 * <ul>
 *     <li><b>Hunt mode:</b> Randomly shoots until a hit is found.</li>
 *     <li><b>Target mode:</b> After a hit, shoots adjacent cells to try
 *     to sink the ship.</li>
 * </ul>
 * When a ship is sunk, the strategy returns to hunt mode.
 */
public class HuntTargetShootingStrategy implements IShootingStrategy, Serializable {

    /**
     * Row index of the last shot fired.
     */
    private int lastRow = -1;

    /**
     * Column index of the last shot fired.
     */
    private int lastCol = -1;

    /**
     * Indicates whether the strategy is currently in target mode.
     */
    private boolean targetMode = false;

    /**
     * Queue of candidate target positions to be fired while in target mode.
     */
    private final Deque<int[]> targets = new ArrayDeque<>();

    /**
     * Random shooting strategy reused for the hunt phase.
     */
    private final RandomShootingStrategy huntStrategy = new RandomShootingStrategy();

    /**
     * Selects the next target position.
     * <p>
     * If the strategy is in target mode and there are pending adjacent
     * targets, the next one is selected from the queue.
     * Otherwise, a random position is selected using the hunt strategy.
     *
     * @param board the game board to analyze
     * @return an array containing the row and column of the selected target
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
     * Executes a shot on the given board.
     * <p>
     * After a hit, the strategy switches to target mode and adds
     * adjacent cells as potential targets.
     * When a ship is sunk, target mode is cleared.
     *
     * @param board the game board where the shot will be executed
     * @return a string representing the result of the shot
     *         (e.g. "hit", "miss", "sunk")
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
     * @return an array containing the row and column of the last shot
     */
    @Override
    public int[] getLastShotCoordinates() {
        return new int[]{lastRow, lastCol};
    }

    /**
     * Adds valid adjacent cells to the target queue.
     * <p>
     * Only cells within bounds and not previously shot are considered.
     *
     * @param board the game board
     * @param r     row of the last hit
     * @param c     column of the last hit
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
