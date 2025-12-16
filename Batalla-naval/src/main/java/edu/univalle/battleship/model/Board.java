package edu.univalle.battleship.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Battleship game board.
 * <p>
 * The board is a fixed-size grid that stores the state of each cell
 * (water, ship, hit, sunk, or miss) and manages ship placement
 * and shot resolution.
 */
public class Board implements Serializable {

    /**
     * Possible logical states of a board cell.
     */
    public enum CellStatus {
        EMPTY, SHIP, HIT, SUNK, MISS
    }

    /**
     * Returns the logical status of a cell based on its internal numeric value.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @return the {@link CellStatus} of the specified cell
     */
    public CellStatus getCellStatus(int row, int col) {
        return switch (cells[row][col]) {
            case 0 -> CellStatus.EMPTY;
            case 1 -> CellStatus.SHIP;
            case 2 -> CellStatus.HIT;
            case 3 -> CellStatus.SUNK;
            case 4 -> CellStatus.MISS;
            default -> throw new IllegalStateException(
                    "Unexpected value: " + cells[row][col]);
        };
    }

    /**
     * Fixed size of the board (10x10).
     */
    public static int SIZE = 10;

    /**
     * Internal representation of the board cells.
     * <p>
     * Values:
     * <ul>
     *   <li>0 = water</li>
     *   <li>1 = ship</li>
     *   <li>2 = hit</li>
     *   <li>3 = sunk</li>
     *   <li>4 = miss</li>
     * </ul>
     */
    private final int[][] cells = new int[SIZE][SIZE];

    /**
     * List of ships currently placed on the board.
     */
    private final List<Ship> ships = new ArrayList<>();

    /**
     * Checks whether a ship can be placed at the specified position
     * and orientation without exceeding the board or overlapping
     * other ships.
     *
     * @param ship the ship to place
     * @param row starting row
     * @param col starting column
     * @param orientation placement orientation
     * @return {@code true} if the ship can be placed, {@code false} otherwise
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
     * Places a ship on the board and updates the internal cell states.
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
     * Returns the internal cell matrix.
     *
     * @return the board cell matrix
     */
    public int[][] getCells() {
        return cells;
    }

    /**
     * Replaces the current cell matrix with a new one.
     *
     * @param newCells the new cell matrix
     */
    public void setCells(int[][] newCells) {
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(newCells[r], 0, cells[r], 0, SIZE);
        }
    }

    /**
     * Processes a shot fired at the specified coordinates.
     *
     * @param row the target row
     * @param col the target column
     * @return a string describing the shot result:
     *         {@code "hit"}, {@code "miss"}, {@code "sunk:ShipName"},
     *         or {@code "already"}
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
     * Returns the ship located at the specified position, if any.
     *
     * @param row row index
     * @param col column index
     * @return the ship at the given position, or {@code null} if none exists
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
     * Checks whether a shot has already been fired at the given position.
     *
     * @param row row index
     * @param col column index
     * @return {@code true} if the cell has already been shot,
     *         {@code false} otherwise
     */
    public boolean isShotRepeated(int row, int col) {
        int value = cells[row][col];
        return value == 2 || value == 3 || value == 4;
    }
}
