package edu.univalle.battleship.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the human player in the Battleship game.
 * <p>
 * Contains the player's board and fleet of ships.
 */
public class Player implements Serializable {

    /** The player's game board. */
    private Board board;

    /** The fleet of ships belonging to the player. */
    private List<Ship> fleet;

    /**
     * Constructs a new {@code Player} with an empty board and an empty fleet.
     */
    public Player() {
        this.board = new Board();
        this.fleet = new ArrayList<>();
    }

    /**
     * Returns the player's board.
     *
     * @return the {@link Board} object of the player
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the player's fleet of ships.
     *
     * @return a list of {@link Ship} objects representing the fleet
     */
    public List<Ship> getFleet() {
        return fleet;
    }

    /**
     * Adds a ship to the player's fleet.
     *
     * @param ship the {@link Ship} to add
     */
    public void addShip(Ship ship) {
        fleet.add(ship);
    }
}
