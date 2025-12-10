package edu.univalle.battleship.model;

import java.io.Serializable;

public class Ship implements Serializable {

    private final String name;
    private final int size;
    private int row;
    private int column;
    private Orientation orientation;

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
}
