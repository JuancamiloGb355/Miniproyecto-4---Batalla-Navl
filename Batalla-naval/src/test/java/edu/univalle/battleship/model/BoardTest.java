package edu.univalle.battleship.model;

import static edu.univalle.battleship.model.Orientation.VERTICAL;
import static edu.univalle.battleship.model.Orientation.HORIZONTAL;
import static org.junit.jupiter.api.Assertions.*;
import edu.univalle.battleship.model.*;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    void assertsRepeatedShotCorrectly() {

        Board board = new Board();
        boolean[][] misses = board.getMisses();
        misses[3][4] = true;
        assertTrue(board.isShotRepeated(3,4));

    }

    @Test
    void assertsNonRepeatedShotCorrectly() {
        Board board = new Board();
        boolean[][] misses = board.getMisses();
        assertFalse(board.isShotRepeated(0,0));

    }

    @Test
    void checksShipPlacementCorrectly() {
        Board board = new Board();
        board.getCells();
        Ship ship = new Ship("Plane",4);
        ship.place(5,4,VERTICAL);
        board.placeShip(ship);
        Ship ship2 = new Ship("Plane2",3);
        assertFalse(board.canPlace(ship2,5,4,HORIZONTAL));
        assertFalse(board.canPlace(ship2,5,4,VERTICAL));
    }

    @Test
    void receivesShotCorrectly() {
        Board board = new Board();
        Ship ship = new Ship("Plane",4);
        ship.place(5,4,VERTICAL);
        board.placeShip(ship);
        assertEquals("hit", board.receiveShot(5,4));
    }

    @Test
    void receivesShotCorrectly2() {
        Board board = new Board();
        Ship ship = new Ship("Plane",1);
        ship.place(5,4,VERTICAL);
        board.placeShip(ship);
        assertEquals("sunk: " + ship.getName(), board.receiveShot(5,4));
    }

    @Test
    void receivesShotCorrectly3() {
        Board board = new Board();
        Ship ship = new Ship("Plane",1);
        ship.place(5,4,VERTICAL);
        board.placeShip(ship);
        assertNotEquals("hit", board.receiveShot(5,4));
    }

    @Test
    void returnsCellStatusCorrectly(){
        Board board = new Board();
        Ship ship = new Ship("Plane",1);
        ship.place(5,4,VERTICAL);
        board.placeShip(ship);
        board.receiveShot(5,4);
        assertEquals(Board.CellStatus.SUNK, board.getCellStatus(5,4));
    }

    @Test
    void returnsShipCorrectly(){
        Board board = new Board();
        Ship ship = new Ship("Plane",1);
        ship.place(5,4,VERTICAL);
        board.placeShip(ship);
        assertEquals(ship,board.getShipAt(5,4));
    }



}