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

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    // Devuelve la cantidad de golpes recibidos
    public int getHits() {
        return hits;
    }

    // Opcional: reiniciar golpes
    public void resetHits() {
        hits = 0;
    }


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

    public int[][] getPositions() {
        int[][] positions = new int[size][2];
        for (int i = 0; i < size; i++) {
            if (orientation == Orientation.HORIZONTAL) {
                positions[i][0] = row;         // fila
                positions[i][1] = column + i;  // columna
            } else {
                positions[i][0] = row + i;     // fila
                positions[i][1] = column;      // columna
            }
        }
        return positions;
    }
}