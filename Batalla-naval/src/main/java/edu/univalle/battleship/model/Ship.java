package edu.univalle.battleship.model;

import java.io.Serializable;

/**
 * Represents a ship in the Battleship game.
 * <p>
 * A ship has a fixed size, a position on the board, an orientation,
 * and keeps track of which of its segments have been hit.
 */
public class Ship implements Serializable {

    /**
     * The display name of the ship.
     */
    private final String name;

    /**
     * The number of cells occupied by the ship.
     */
    private final int size;

    /**
     * Starting row position of the ship on the board.
     */
    private int row;

    /**
     * Starting column position of the ship on the board.
     */
    private int column;

    /**
     * Orientation of the ship (horizontal or vertical).
     */
    private Orientation orientation;

    /**
     * One hit flag per ship segment.
     * Each index represents whether that segment has been hit.
     */
    private boolean[] hits;

    /**
     * Creates a ship with a given name and size.
     *
     * @param name the name of the ship
     * @param size the size (number of cells) of the ship
     */
    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        this.hits = new boolean[size];
    }

    /**
     * Returns the name of the ship.
     *
     * @return the ship's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the size of the ship.
     *
     * @return the ship size
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the starting row of the ship.
     *
     * @return the row position
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the starting column of the ship.
     *
     * @return the column position
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns the orientation of the ship.
     *
     * @return the ship orientation
     */
    public Orientation getOrientation() {
        return orientation;
    }

    // -----------------------------
    // Ship placement
    // -----------------------------

    /**
     * Places the ship at the given position with the specified orientation.
     *
     * @param row the starting row
     * @param column the starting column
     * @param orientation the ship orientation
     */
    public void place(int row, int column, Orientation orientation) {
        this.row = row;
        this.column = column;
        this.orientation = orientation;
    }

    // -----------------------------
    // Register a hit on a specific cell
    // -----------------------------

    /**
     * Registers a hit on the ship at the given board coordinates.
     *
     * @param r the row of the hit
     * @param c the column of the hit
     */
    public void hitAt(int r, int c) {
        int[][] pos = getPositions();
        for (int i = 0; i < pos.length; i++) {
            if (pos[i][0] == r && pos[i][1] == c) {
                hits[i] = true;
                return;
            }
        }
    }

    /**
     * Legacy method kept for backward compatibility.
     * <p>
     * This method intentionally does nothing to avoid incorrect
     * global hit registration.
     */
    public void hit() {
        // Intentionally left blank
    }

    // -----------------------------
    // Ship state
    // -----------------------------

    /**
     * Determines whether the ship is completely sunk.
     *
     * @return {@code true} if all ship segments have been hit, {@code false} otherwise
     */
    public boolean isSunk() {
        for (boolean h : hits) {
            if (!h) return false;
        }
        return true;
    }

    // -----------------------------
    // Occupied positions
    // -----------------------------

    /**
     * Returns all board positions occupied by the ship.
     *
     * @return a 2D array containing the row and column of each ship segment
     */
    public int[][] getPositions() {
        int[][] positions = new int[size][2];
        for (int i = 0; i < size; i++) {
            if (orientation == Orientation.HORIZONTAL) {
                positions[i][0] = row;
                positions[i][1] = column + i;
            } else {
                positions[i][0] = row + i;
                positions[i][1] = column;
            }
        }
        return positions;
    }

    /**
     * Returns a copy of the hit status array.
     *
     * @return a cloned array representing hit segments
     */
    public boolean[] getHitsArray() {
        return hits.clone();
    }

    /**
     * Restores the hit status of the ship from saved data.
     *
     * @param savedHits the saved hit array
     */
    public void restoreHits(boolean[] savedHits) {
        if (savedHits != null && savedHits.length == size) {
            this.hits = savedHits.clone();
        }
    }
}
