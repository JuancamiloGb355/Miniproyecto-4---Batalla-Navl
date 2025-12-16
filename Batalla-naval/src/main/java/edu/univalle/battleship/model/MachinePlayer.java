package edu.univalle.battleship.model;

import edu.univalle.battleship.designpatterns.strategy.HuntTargetShootingStrategy;
import edu.univalle.battleship.designpatterns.strategy.IShootingStrategy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the computer-controlled player.
 * <p>
 * The machine player has its own board and fleet, and it can place ships
 * and make shots automatically using a shooting strategy.
 */
public class MachinePlayer implements Serializable {

    private Board board;
    private List<Ship> fleet;
    private IShootingStrategy strategy;
    private Random random;

    /**
     * Constructs a new MachinePlayer with an empty board and fleet,
     * using the HuntTargetShootingStrategy by default.
     */
    public MachinePlayer() {
        this.board = new Board();
        this.fleet = new ArrayList<>();
        this.strategy = new HuntTargetShootingStrategy(); // AI strategy
        this.random = new Random();
    }

    /**
     * Returns the board of the machine player.
     *
     * @return the machine player's board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the fleet of ships belonging to the machine player.
     *
     * @return list of ships
     */
    public List<Ship> getFleet() {
        return fleet;
    }

    /**
     * Automatically places a standard fleet on the board.
     * <p>
     * Uses the same ship sizes as in the PositionController.
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

        // Try to place each ship randomly on the board
        for (Ship ship : fleet) {
            boolean placed = false;

            while (!placed) {
                int row = random.nextInt(Board.SIZE);
                int col = random.nextInt(Board.SIZE);

                Orientation orientation =
                        random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

                if (board.canPlace(ship, row, col, orientation)) {
                    ship.place(row, col, orientation);     // initial position
                    board.placeShip(ship);                 // place ship on board
                    placed = true;
                }
            }
        }
    }

    /**
     * Returns the coordinates of the last shot fired by the machine.
     *
     * @return array with two integers: {row, column}
     */
    public int[] getLastShotCoordinates() {
        return strategy.getLastShotCoordinates();
    }

    /**
     * Fires a shot at the given player's board using the machine's strategy.
     * <p>
     * Returns the result of the shot: "hit", "miss", "sunk:ShipName", etc.
     *
     * @param player the player to shoot at
     * @return a string describing the result of the shot
     */
    public String shoot(Player player) {
        return strategy.shoot(player.getBoard());
    }
}
