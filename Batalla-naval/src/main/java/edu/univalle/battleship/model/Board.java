package edu.univalle.battleship.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {

    public enum CellStatus {
        EMPTY, SHIP, HIT, SUNK, MISS
    }

    public CellStatus getCellStatus(int row, int col) {
        return switch (cells[row][col]) {
            case 0 -> CellStatus.EMPTY;
            case 1 -> CellStatus.SHIP;
            case 2 -> CellStatus.HIT;
            case 3 -> CellStatus.SUNK;
            case 4 -> CellStatus.MISS;
            default -> throw new IllegalStateException("Unexpected value: " + cells[row][col]);
        };
    }


    public static int SIZE = 10;
    private final int[][] cells = new int[SIZE][SIZE]; // 0=agua,1=barco,2=hit,3=sunk,4=miss
    private final List<Ship> ships = new ArrayList<>();

    public boolean canPlace(Ship ship, int row, int col, Orientation orientation) {
        int dx = orientation == Orientation.HORIZONTAL ? 0 : 1;
        int dy = orientation == Orientation.HORIZONTAL ? 1 : 0;

        for (int i = 0; i < ship.getSize(); i++) {
            int r = row + i * dx;
            int c = col + i * dy;

            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) return false;
            if (cells[r][c] != 0) return false;
        }
        return true;
    }

    public void placeShip(Ship ship) {
        int row = ship.getRow();
        int col = ship.getColumn();
        Orientation orientation = ship.getOrientation();

        int dx = orientation == Orientation.HORIZONTAL ? 0 : 1;
        int dy = orientation == Orientation.HORIZONTAL ? 1 : 0;

        for (int i = 0; i < ship.getSize(); i++) {
            cells[row + i * dx][col + i * dy] = 1;
        }

        if (!ships.contains(ship)) {
            ships.add(ship);
        }
    }

    public int[][] getCells() {
        return cells;
    }

    public void setCells(int[][] newCells) {
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(newCells[r], 0, cells[r], 0, SIZE);
        }
    }

    public String receiveShot(int row, int col) {
        // 0 = agua, 1 = barco, 2 = hit, 3 = sunk, 4 = miss
        if (cells[row][col] == 1) { // hay barco
            cells[row][col] = 2; // marcar hit

            Ship hitShip = getShipAt(row, col);
            if (hitShip != null) {
                hitShip.hit(); // registrar hit
                if (hitShip.isSunk()) {
                    for (int[] pos : hitShip.getPositions()) {
                        cells[pos[0]][pos[1]] = 3; // todas las celdas como sunk
                    }
                    return "sunk:" + hitShip.getName();
                }
            }
            return "hit";
        } else if (cells[row][col] == 0) {
            cells[row][col] = 4; // miss
            return "miss";
        } else {
            // ya fue disparado
            return "already";
        }
    }



    public Ship getShipAt(int row, int col) {
        for (Ship ship : ships) {
            int[][] pos = ship.getPositions();
            for (int[] p : pos) {
                if (p[0] == row && p[1] == col) return ship;
            }
        }
        return null;
    }

    public boolean allShipsSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) return false;
        }
        return true;
    }

    public void clear() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c] = 0;
            }
        }
        ships.clear();
    }

    // Métodos de utilidad para el UI
    public boolean isShotRepeated(int row, int col) {
        int value = cells[row][col];
        return value == 2 || value == 3 || value == 4;
    }

    public void rebuildShipsHitsFromCells() {
        for (Ship ship : ships) {
            int hitsCount = 0;
            for (int[] pos : ship.getPositions()) {
                int r = pos[0];
                int c = pos[1];
                if (cells[r][c] == 2 || cells[r][c] == 3) hitsCount++;
            }
            for (int i = 0; i < hitsCount; i++) {
                ship.hit(); // registra los golpes previos
            }
        }
    }


}
