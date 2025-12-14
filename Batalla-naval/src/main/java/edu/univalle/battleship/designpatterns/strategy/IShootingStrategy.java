package edu.univalle.battleship.designpatterns.strategy;

import edu.univalle.battleship.model.Board;

public interface IShootingStrategy {
    /**
     * Dispara a un tablero y devuelve el resultado del tiro:
     * "hit", "miss", "sunk:Nombre", etc.
     */
    String shoot(Board board);
}
