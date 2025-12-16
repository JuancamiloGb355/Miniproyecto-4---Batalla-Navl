package edu.univalle.battleship.designpatterns.strategy;

import edu.univalle.battleship.model.Board;
import java.util.Random;

public class RandomShootingStrategy implements IShootingStrategy {

    private int lastRow;
    private int lastCol;
    private final Random random = new Random();

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

    @Override
    public String shoot(Board board) {
        int[] pos = selectTarget(board);
        lastRow = pos[0];
        lastCol = pos[1];
        return board.receiveShot(lastRow, lastCol);
    }


    @Override
    public int[] getLastShotCoordinates() {
        return new int[]{lastRow, lastCol};
    }
}
