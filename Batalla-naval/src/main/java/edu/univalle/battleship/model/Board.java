package edu.univalle.battleship.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {

    public static final int SIZE = 10;
    private final Ship[][] cells = new Ship[SIZE][SIZE];

    // para guardar disparos hechos
    private boolean[][] hits = new boolean[SIZE][SIZE];
    private boolean[][] misses = new boolean[SIZE][SIZE];

    // lista de barcos (para hundidos y victoria)
    private List<Ship> ships = new ArrayList<>();

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

        ships.add(ship);

        for (int i = 0; i < ship.getSize(); i++) {
            cells[row + i * dx][col + i * dy] = ship;
        }
    }

    public Ship[][] getCells() {
        return cells;
    }


    //disparos
    public boolean isShotRepeated(int row, int col) {
        return hits[row][col] || misses[row][col];
    }

    public String receiveShot(int row, int col) {

        if (isShotRepeated(row, col)) {
            return "repeat";
        }

        Ship ship = cells[row][col];

        if (ship != null) { // TOCADO
            hits[row][col] = true;
            ship.hit();

            if (ship.isSunk()) {
                return "sunk:" + ship.getName();
            }
            return "hit";
        } else { // AGUA
            misses[row][col] = true;
            return "miss";
        }
    }


    //victoria
    public boolean allShipsSunk() {
        for (Ship s : ships) {
            if (!s.isSunk()) return false;
        }
        return true;
    }


    //acceso a barcos
    public List<Ship> getShips() {
        return ships;
    }
}