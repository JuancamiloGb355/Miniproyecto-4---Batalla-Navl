package edu.univalle.battleship.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a human player in the Battleship game.
 * <p>
 * A player owns a board and a fleet of ships placed on that board.
 */
public class Player implements Serializable {

    /**
     * The game board associated with the player.
     */
    private Board board;

    /**
     * The list of ships belonging to the player's fleet.
     */
    private List<Ship> fleet;

    /**
     * Creates a new player with an empty board and an empty fleet.
     */
    public Player() {
        this.board = new Board();
        this.fleet = new ArrayList<>();
    }

    /**
     * Returns the player's board.
     *
     * @return the player's {@link Board}
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the player's fleet of ships.
     *
     * @return a list containing the player's ships
     */
    public List<Ship> getFleet() {
        return fleet;
    }

    /**
     * Adds a ship to the player's fleet.
     *
     * @param ship the ship to be added
     */
    public void addShip(Ship ship) {
        fleet.add(ship);
    }
}
