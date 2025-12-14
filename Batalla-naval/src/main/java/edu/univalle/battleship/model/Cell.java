package edu.univalle.battleship.model;

public class Cell {
// Representa cada celda del tablero.
// Tiene un estado: agua, barco, tocada, hundida, disparo fallido.


    private Ship ship;
    private boolean hit;
    private boolean miss;


    public boolean hasShip() {
        return ship != null;
    }


    public void setShip(Ship ship) {
        this.ship = ship;
    }


    public boolean isHit() {
        return hit;
    }


    public void markHit() {
        this.hit = true;
        this.miss = false;

        if (ship != null) {
            ship.hit();
        }
    }


    public void markMiss() {
        this.miss = true;
        this.hit = false;
    }


    public boolean isMiss() {
        return miss;
    }


    public Ship getShip() {
        return ship;
    }
}

