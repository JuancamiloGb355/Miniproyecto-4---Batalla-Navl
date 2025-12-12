package edu.univalle.battleship.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {

    public static int SIZE = 10;
    private final Ship[][] cells = new Ship[SIZE][SIZE];
    private int sunkenShips = 0;


    // para guardar disparos hechos
    private boolean[][] hits = new boolean[SIZE][SIZE];
    private boolean[][] misses = new boolean[SIZE][SIZE];

    // lista de barcos (para hundidos y victoria)
    private List<Ship> ships = new ArrayList<>();

    public enum CellStatus {
        EMPTY, HIT, MISS, SHIP, SUNK
    }

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

        if (!ships.contains(ship)) {
            ships.add(ship);
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

        if (ship != null) {
            hits[row][col] = true;
            ship.hit();

            if (ship.isSunk()) {
                sunkenShips++;
                return "sunk:" + ship.getName();
            }
            return "hit";
        } else {
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

    /**
     * Devuelve el barco que se encuentra en la celda indicada,
     * o null si no hay ninguno.
     */
    public Ship getShipAt(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return null;
        return cells[row][col];
    }

    /**
     * Devuelve el estado de la celda indicada.
     */
    public CellStatus getCellStatus(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return null;

        if (hits[row][col]) return CellStatus.HIT;
        if (misses[row][col]) return CellStatus.MISS;

        Ship ship = cells[row][col];
        if (ship != null && ship.isSunk()) return CellStatus.SUNK;
        if (ship != null) return CellStatus.SHIP;

        return CellStatus.EMPTY;
    }

    public boolean[][] getMisses() {
        return misses;
    }

    public boolean[][] getHits() {
        return hits;
    }

    public int getSunkenShips() {
        return sunkenShips;
    }

}
