package edu.univalle.battleship.designpatterns.strategy;

import edu.univalle.battleship.model.Board;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

public class HuntTargetShootingStrategy implements IShootingStrategy, Serializable {

    private int lastRow = -1;
    private int lastCol = -1;

    private boolean targetMode = false;
    private final Deque<int[]> targets = new ArrayDeque<>();

    // Reutilizamos el HUNT random
    private final RandomShootingStrategy huntStrategy = new RandomShootingStrategy();

    // ----------------------------------
    // Decide dónde disparar
    // ----------------------------------
    @Override
    public int[] selectTarget(Board board) {

        if (targetMode && !targets.isEmpty()) {
            return targets.poll();
        }

        targetMode = false;
        return huntStrategy.selectTarget(board);
    }

    // ----------------------------------
    // Ejecuta el disparo
    // ----------------------------------
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

    @Override
    public int[] getLastShotCoordinates() {
        return new int[]{lastRow, lastCol};
    }

    // ----------------------------------
    // Añade vecinos para TARGET
    // ----------------------------------
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
