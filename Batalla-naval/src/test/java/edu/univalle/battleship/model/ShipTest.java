package edu.univalle.battleship.model;

import static org.junit.jupiter.api.Assertions.*;
import edu.univalle.battleship.model.*;
import org.junit.jupiter.api.Test;

class ShipTest {

    @Test
    void checksIfShipIsSunkCorrectly(){
        Ship ship = new Ship("Plane",3);
        ship.hit();
        ship.hit();
        assertFalse(ship.isSunk());
    }

    @Test
    void checksIfShipIsSunkCorrectly2(){
        Ship ship = new Ship("Plane",3);
        ship.hit();
        ship.hit();
        ship.hit();
        assertTrue(ship.isSunk());
    }

}