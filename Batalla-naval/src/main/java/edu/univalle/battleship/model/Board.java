package edu.univalle.battleship.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game board in Battleship.
 * <p>
 * Holds the grid of cells and the ships placed on the board.
 * Provides methods for placing ships, receiving shots, and checking cell status.
 */
public class Board implements Serializable {

    /**
     * Represents the status of a single cell on the board.
     */
    public enum CellStatus {
        EMPTY,  // No ship, untouched
        SHIP,   // Ship is placed
        HIT,    // Ship hit
        SUNK,   // Ship sunk
        MISS    // Shot missed
    }

    /** The size of the board (NxN). */
    public static int SIZE = 10;

    /** Internal representation of cells: 0=water, 1=ship, 2=hit, 3=sunk, 4=miss */
    private final int[][] cells = new int[SIZE][SIZE];

    /** List of ships placed on the board. */
    private final List<Ship> ships = new ArrayList<>();

    /**
     * Returns the status of a specific cell.
     *
     * @param row the row index
     * @param col the column index
     * @return the CellStatus of the cell
     */
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

    /**
     * Checks if a ship can be placed at the given position with the specified orientation.
     *
     * @param ship the ship to place
     * @param row starting row
     * @param col starting column
     * @param orientation ship orientation (HORIZONTAL or VERTICAL)
     * @return true if the ship can be placed, false otherwise
     */
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

    /**
     * Places a ship on the board at its current position and orientation.
     *
     * @param ship the ship to place
     */
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

    /**
     * Returns the 2D array representing the cells of the board.
     *
     * @return a 2D int array of the board
     */
    public int[][] getCells() {
        return cells;
    }

    /**
     * Sets the board's cells from an external 2D array.
     *
     * @param newCells the new cell values
     */
    public void setCells(int[][] newCells) {
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(newCells[r], 0, cells[r], 0, SIZE);
        }
    }

    /**
     * Processes a shot at a specific cell.
     *
     * @param row the row index
     * @param col the column index
     * @return "hit", "miss", "sunk:ShipName", or "already" if the cell was previously targeted
     */
    public String receiveShot(int row, int col) {

        if (cells[row][col] == 1) {
            Ship hitShip = getShipAt(row, col);
            cells[row][col] = 2;

            if (hitShip != null) {
                hitShip.hitAt(row, col);

                if (hitShip.isSunk()) {
                    for (int[] pos : hitShip.getPositions()) {
                        cells[pos[0]][pos[1]] = 3;
                    }
                    return "sunk:" + hitShip.getName();
                }
            }
            return "hit";
        }

        if (cells[row][col] == 0) {
            cells[row][col] = 4;
            return "miss";
        }

        return "already";
    }

    /**
     * Returns the ship located at a specific cell.
     *
     * @param row the row index
     * @param col the column index
     * @return the Ship at the cell or null if no ship is present
     */
    public Ship getShipAt(int row, int col) {
        for (Ship ship : ships) {
            for (int[] pos : ship.getPositions()) {
                if (pos[0] == row && pos[1] == col) {
                    return ship;
                }
            }
        }
        return null;
    }

    /**
     * Checks if a shot has already been made at a specific cell.
     *
     * @param row the row index
     * @param col the column index
     * @return true if the cell has been hit, missed, or sunk
     */
    public boolean isShotRepeated(int row, int col) {
        int value = cells[row][col];
        return value == 2 || value == 3 || value == 4;
    }
}
