package edu.univalle.battleship.model;

import edu.univalle.battleship.designpatterns.strategy.HuntTargetShootingStrategy;
import edu.univalle.battleship.designpatterns.strategy.IShootingStrategy;
import edu.univalle.battleship.designpatterns.strategy.RandomShootingStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representa a la maquina
 * Tiene su tablero y coloca o dispara automaticamente.
 */
public class MachinePlayer implements Serializable {

    private Board board;
    private List<Ship> fleet;
    private IShootingStrategy strategy;

    private Random random;

    public MachinePlayer() {
        this.board = new Board();
        this.fleet = new ArrayList<>();
        this.strategy = new HuntTargetShootingStrategy(); //ELEGIR LA IA
        this.random = new Random();
    }

    public Board getBoard() {
        return board;
    }

    public List<Ship> getFleet() {
        return fleet;
    }

    /**
     * Coloca automaticamente una flota estandar en el tablero.
     * Usa el mismo sistema de tamaños que en PositionController.
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

        // Intentar colocar cada barco aleatoriamente
        for (Ship ship : fleet) {
            boolean placed = false;

            while (!placed) {

                int row = random.nextInt(Board.SIZE);
                int col = random.nextInt(Board.SIZE);

                Orientation orientation =
                        random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

                if (board.canPlace(ship, row, col, orientation)) {
                    ship.place(row, col, orientation);     // posicion inicial
                    board.placeShip(ship);                 // colocarlo en Board
                    placed = true;
                }
            }
        }
    }

    public int[] getLastShotCoordinates() {
        return strategy.getLastShotCoordinates();
    }



    /**
     * La maquina dispara al tablero del jugador usando una estrategia.
     * Retorna el resultado ("hit", "miss", "sunk:Nombre", etc).
     */
    public String shoot(Player player) {
        return strategy.shoot(player.getBoard());
    }
}

