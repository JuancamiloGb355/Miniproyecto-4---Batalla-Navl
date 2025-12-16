package edu.univalle.battleship.designpatterns.strategy;

import edu.univalle.battleship.model.Board;

/**
 * Defines the interface for a shooting strategy used by the machine player.
 * <p>
 * Implementations of this interface determine how the machine selects targets and fires at a board.
 */
public interface IShootingStrategy {

    /**
     * Fires a shot at the given board and returns the result.
     *
     * @param board the board to shoot at
     * @return the result of the shot, e.g., "hit", "miss", "sunk:ShipName", or "already"
     */
    String shoot(Board board);

    /**
     * Returns the coordinates of the last shot fired.
     *
     * @return an array {row, column} representing the last shot
     */
    int[] getLastShotCoordinates();

    /**
     * Selects the next target cell to shoot at on the given board.
     *
     * @param board the board to select a target from
     * @return an array {row, column} representing the selected target
     */
    int[] selectTarget(Board board);
}
