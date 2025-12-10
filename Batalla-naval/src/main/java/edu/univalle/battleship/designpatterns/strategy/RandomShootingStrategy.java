package edu.univalle.battleship.designpatterns.strategy;

import edu.univalle.battleship.model.Board;
import edu.univalle.battleship.model.Ship;

import java.util.Random;

public class RandomShootingStrategy implements IShootingStrategy {

    private final Random random = new Random();

    @Override
    public String shoot(Board board) {
        int row, col;

        // Buscar una celda válida donde no se haya disparado
        do {
            row = random.nextInt(Board.SIZE);
            col = random.nextInt(Board.SIZE);
        } while (board.isShotRepeated(row, col));

        // Disparar al tablero y devolver el resultado
        return board.receiveShot(row, col);
    }
}
