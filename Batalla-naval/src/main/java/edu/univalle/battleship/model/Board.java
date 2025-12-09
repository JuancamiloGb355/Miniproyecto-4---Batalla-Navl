package edu.univalle.battleship.model;

import java.io.Serializable;

public class Board implements Serializable {

    private final int SIZE = 10;
    private final Ship[][] cells = new Ship[SIZE][SIZE];

    public boolean canPlace(Ship ship, int row, int col, Orientation orientation) {

        int dx = (orientation == Orientation.HORIZONTAL) ? 0 : 1;
        int dy = (orientation == Orientation.HORIZONTAL) ? 1 : 0;

        for (int i = 0; i < ship.getSize(); i++) {
            int r = row + i * dx;
            int c = col + i * dy;

            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) return false;
            if (cells[r][c] != null) return false;
        }
        return true;
    }

    public void placeShip(Ship ship) {
        int row = ship.getRow();
        int col = ship.getColumn();
        Orientation orientation = ship.getOrientation();

        int dx = (orientation == Orientation.HORIZONTAL) ? 0 : 1;
        int dy = (orientation == Orientation.HORIZONTAL) ? 1 : 0;

        for (int i = 0; i < ship.getSize(); i++) {
            cells[row + i * dx][col + i * dy] = ship;
        }
    }

    public Ship[][] getCells() {
        return cells;
    }
}
