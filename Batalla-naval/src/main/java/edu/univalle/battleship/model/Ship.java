package edu.univalle.battleship.model;

import java.io.Serializable;

/**
 * Represents a ship in the Battleship game.
 * <p>
 * Each ship has a name, size, orientation, starting position, and keeps track of hits per cell.
 */
public class Ship implements Serializable {

    /** The name of the ship (e.g., "Carrier", "Submarine"). */
    private final String name;

    /** The size of the ship (number of cells it occupies). */
    private final int size;

    /** The starting row of the ship on the board. */
    private int row;

    /** The starting column of the ship on the board. */
    private int column;

    /** The orientation of the ship (HORIZONTAL or VERTICAL). */
    private Orientation orientation;

    /** Tracks hits on each cell of the ship. */
    private boolean[] hits;

    /**
     * Constructs a new {@code Ship} with the specified name and size.
     *
     * @param name the name of the ship
     * @param size the size of the ship
     */
    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        this.hits = new boolean[size];
    }

    /** @return the name of the ship */
    public String getName() {
        return name;
    }

    /** @return the size of the ship */
    public int getSize() {
        return size;
    }

    /** @return the starting row of the ship */
    public int getRow() {
        return row;
    }

    /** @return the starting column of the ship */
    public int getColumn() {
        return column;
    }

    /** @return the orientation of the ship */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Places the ship at the specified starting position with the given orientation.
     *
     * @param row the starting row
     * @param column the starting column
     * @param orientation the orientation of the ship
     */
    public void place(int row, int column, Orientation orientation) {
        this.row = row;
        this.column = column;
        this.orientation = orientation;
    }

    /**
     * Registers a hit on the ship at the specified cell coordinates.
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
     * Legacy method for compatibility; does nothing.
     * <p>
     * Avoids issues with global hit tracking.
     */
    public void hit() {
        // intentionally left blank
    }

    /**
     * Checks if the ship is completely sunk.
     *
     * @return {@code true} if all cells of the ship have been hit, {@code false} otherwise
     */
    public boolean isSunk() {
        for (boolean h : hits) {
            if (!h) return false;
        }
        return true;
    }

    /**
     * Returns the positions occupied by this ship on the board.
     *
     * @return a 2D array where each element is a {row, column} coordinate of the ship
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
     * Returns a copy of the hits array for external inspection.
     *
     * @return a boolean array indicating hits on each cell
     */
    public boolean[] getHitsArray() {
        return hits.clone();
    }

    /**
     * Restores the hits array from a previously saved state.
     *
     * @param savedHits the saved hits array
     */
    public void restoreHits(boolean[] savedHits) {
        if (savedHits != null && savedHits.length == size) {
            this.hits = savedHits.clone();
        }
    }
}
