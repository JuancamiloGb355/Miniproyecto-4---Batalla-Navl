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

        // Comprobar si la celda ya fue disparada
        do {
            row = random.nextInt(Board.SIZE);
            col = random.nextInt(Board.SIZE);
        } while (board.getCells()[row][col] >= 2); // >=2 significa hit, sunk o miss

        lastRow = row;
        lastCol = col;

        return board.receiveShot(row, col);
    }

    public int[] getLastShotCoordinates() {
        return new int[]{lastRow, lastCol};
    }
}
