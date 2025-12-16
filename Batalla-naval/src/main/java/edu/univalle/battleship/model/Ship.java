package edu.univalle.battleship.model;

import java.io.Serializable;

public class Ship implements Serializable {

    private final String name;
    private final int size;
    private int row;
    private int column;
    private Orientation orientation;

    // 🔥 un golpe por cada celda del barco
    private boolean[] hits;

    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        this.hits = new boolean[size];
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

    // -----------------------------
    // Colocar barco
    // -----------------------------
    public void place(int row, int column, Orientation orientation) {
        this.row = row;
        this.column = column;
        this.orientation = orientation;
    }

    // -----------------------------
    // Registrar golpe por celda
    // -----------------------------
    public void hitAt(int r, int c) {
        int[][] pos = getPositions();
        for (int i = 0; i < pos.length; i++) {
            if (pos[i][0] == r && pos[i][1] == c) {
                hits[i] = true;
                return;
            }
        }
    }

    // 🔁 compatibilidad con código viejo
    public void hit() {
        // NO hacer nada: se evita el bug de hits globales
    }

    // -----------------------------
    // Saber si está hundido
    // -----------------------------
    public boolean isSunk() {
        for (boolean h : hits) {
            if (!h) return false;
        }
        return true;
    }

    // -----------------------------
    // Posiciones ocupadas
    // -----------------------------
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

    public boolean[] getHitsArray() {
        return hits.clone(); // clone para seguridad
    }

    public void restoreHits(boolean[] savedHits) {
        if (savedHits != null && savedHits.length == size) {
            this.hits = savedHits.clone();
        }
    }

}
