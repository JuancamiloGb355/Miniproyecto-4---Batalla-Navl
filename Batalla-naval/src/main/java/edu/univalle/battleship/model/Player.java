package edu.univalle.battleship.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa al jugador humano.
 * Contiene su tablero y su flota de barcos.
 */
public class Player implements Serializable {

    private Board board;
    private List<Ship> fleet;

    public Player() {
        this.board = new Board();
        this.fleet = new ArrayList<>();
    }

    public Board getBoard() {
        return board;
    }

    public List<Ship> getFleet() {
        return fleet;
    }

    /**
     * Agrega un barco a la flota del jugador.
     */
    public void addShip(Ship ship) {
        fleet.add(ship);
    }

    /**
     * Devuelve true si todos los barcos del jugador estan hundidos.
     */
    public boolean hasLost() {
        return board.allShipsSunk();
    }
}