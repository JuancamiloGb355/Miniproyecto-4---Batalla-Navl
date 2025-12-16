package edu.univalle.battleship.model;

import edu.univalle.battleship.designpatterns.strategy.HuntTargetShootingStrategy;
import edu.univalle.battleship.designpatterns.strategy.IShootingStrategy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the machine-controlled player in the Battleship game.
 * <p>
 * The machine player owns its own board, fleet of ships, and
 * automatically places ships and fires shots using a shooting strategy.
 */
public class MachinePlayer implements Serializable {

    /**
     * Board owned by the machine player.
     */
    private Board board;

    /**
     * Fleet of ships controlled by the machine.
     */
    private List<Ship> fleet;

    /**
     * Shooting strategy used by the machine player.
     */
    private IShootingStrategy strategy;

    /**
     * Random generator used for automatic ship placement.
     */
    private Random random;

    /**
     * Creates a new MachinePlayer with an empty board and fleet.
     * <p>
     * By default, the machine uses a Hunt-Target shooting strategy.
     */
    public MachinePlayer() {
        this.board = new Board();
        this.fleet = new ArrayList<>();
        this.strategy = new HuntTargetShootingStrategy(); // AI strategy selection
        this.random = new Random();
    }

    /**
     * Returns the machine's board.
     *
     * @return the board owned by the machine
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the machine's fleet of ships.
     *
     * @return the list of ships controlled by the machine
     */
    public List<Ship> getFleet() {
        return fleet;
    }

    /**
     * Automatically places a standard fleet of ships on the board.
     * <p>
     * The fleet composition and ship sizes are consistent with the
     * configuration used in the positioning phase of the game.
     * Ships are placed randomly while respecting board boundaries
     * and avoiding overlaps.
     */
    public void placeFleetAutomatically() {

        fleet.add(new Ship("Carrier", 4));
        fleet.add(new Ship("Submarine 1", 3));
        fleet.add(new Ship("Submarine 2", 3));
        fleet.add(new Ship("Destroyer 1", 2));
        fleet.add(new Ship("Destroyer 2", 2));
        fleet.add(new Ship("Destroyer 3", 2));
        fleet.add(new Ship("Patrol 1", 1));
        fleet.add(new Ship("Patrol 2", 1));
        fleet.add(new Ship("Patrol 3", 1));
        fleet.add(new Ship("Patrol 4", 1));

        // Attempt to place each ship randomly on the board
        for (Ship ship : fleet) {
            boolean placed = false;

            while (!placed) {

                int row = random.nextInt(Board.SIZE);
                int col = random.nextInt(Board.SIZE);

                Orientation orientation =
                        random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

                if (board.canPlace(ship, row, col, orientation)) {
                    ship.place(row, col, orientation); // initial position
                    board.placeShip(ship);             // place on board
                    placed = true;
                }
            }
        }
    }

    /**
     * Returns the coordinates of the last shot fired by the machine.
     *
     * @return an array containing the row and column of the last shot
     */
    public int[] getLastShotCoordinates() {
        return strategy.getLastShotCoordinates();
    }

    /**
     * Fires a shot at the human player's board using the configured
     * shooting strategy.
     *
     * @param player the human player being targeted
     * @return a string describing the result of the shot
     *         (e.g., "hit", "miss", "sunk:ShipName")
     */
    public String shoot(Player player) {
        return strategy.shoot(player.getBoard());
    }
}
