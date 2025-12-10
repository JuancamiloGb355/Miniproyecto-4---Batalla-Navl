package edu.univalle.battleship.model;

import static org.junit.jupiter.api.Assertions.*;
import edu.univalle.battleship.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

class PlayerTest {

    @Test
    void returnsBoardCorrectly(){
        Player player = new Player();
        Board board = player.getBoard();
        assertNotNull(board);

    }

    @Test
    void returnsFleetCorrectly(){
        Player player = new Player();
        assertNotNull(player.getFleet());

    }

    @Test
    void addsShipsCorrectly(){
        Player player = new Player();
        Ship ship = new Ship("Plane",4);
        player.addShip(ship);
        List<Ship> ships = player.getFleet();
        assertNotNull(ships.get(0));


    }
}