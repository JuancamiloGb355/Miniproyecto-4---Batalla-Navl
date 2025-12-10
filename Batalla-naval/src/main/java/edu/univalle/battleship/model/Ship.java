package edu.univalle.battleship.model;

import java.io.Serializable;

public class Ship implements Serializable {

    private final String name;
    private final int size;
    private int row;
    private int column;
    private Orientation orientation;

    //golpes recibidos
    private int hits = 0;

    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() { return name; }
    public int getSize() { return size; }
    public int getRow() { return row; }
    public int getColumn() { return column; }
    public Orientation getOrientation() { return orientation; }

    public void place(int row, int column, Orientation orientation) {
        this.row = row;
        this.column = column;
        this.orientation = orientation;
    }

    //registrar un golpe
    public void hit() {
        if (hits < size) {
            hits++;
        }
    }

    // saber si esta hundido
    public boolean isSunk() {
        return hits >= size;
    }


    public boolean occupies(int r, int c) {
        if (orientation == Orientation.HORIZONTAL) {
            return r == row && c >= column && c < column + size;
        } else {
            return c == column && r >= row && r < row + size;
        }
    }
}