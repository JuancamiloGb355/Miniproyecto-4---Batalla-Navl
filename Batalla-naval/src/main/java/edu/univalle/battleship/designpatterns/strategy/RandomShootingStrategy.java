package edu.univalle.battleship.designpatterns.strategy;

import edu.univalle.battleship.model.Board;
import java.util.Random;

public class RandomShootingStrategy implements IShootingStrategy {

    private final Random random = new Random();

    private int lastRow;
    private int lastCol;

    @Override
    public String shoot(Board board) {
        int row, col;

        do {
            row = random.nextInt(Board.SIZE);
            col = random.nextInt(Board.SIZE);
        } while (board.isShotRepeated(row, col));

        lastRow = row;
        lastCol = col;

        return board.receiveShot(row, col);
    }

    public int[] getLastShotCoordinates() {
        return new int[]{lastRow, lastCol};
    }
}