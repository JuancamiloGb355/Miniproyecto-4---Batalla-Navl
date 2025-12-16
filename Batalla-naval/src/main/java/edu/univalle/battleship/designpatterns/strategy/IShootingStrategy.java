package edu.univalle.battleship.designpatterns.strategy;

import edu.univalle.battleship.model.Board;

/**
 * Strategy interface for defining different shooting behaviors
 * in the Battleship game.
 * <p>
 * Implementations of this interface encapsulate the logic used
 * to select a target and execute a shot on a game board.
 */
public interface IShootingStrategy {

    /**
     * Executes a shot on the given board and returns the result.
     * <p>
     * Possible return values include:
     * <ul>
     *     <li>{@code "hit"}</li>
     *     <li>{@code "miss"}</li>
     *     <li>{@code "sunk:ShipName"}</li>
     * </ul>
     *
     * @param board the game board where the shot will be executed
     * @return a string describing the result of the shot
     */
    String shoot(Board board);

    /**
     * Returns the coordinates of the last shot fired by the strategy.
     *
     * @return an array containing the row and column of the last shot
     */
    int[] getLastShotCoordinates();

    /**
     * Selects the next target position on the board.
     * <p>
     * This method determines where the next shot will be aimed,
     * but does not execute the shot.
     *
     * @param board the game board to analyze
     * @return an array containing the row and column of the selected target
     */
    int[] selectTarget(Board board);

}
