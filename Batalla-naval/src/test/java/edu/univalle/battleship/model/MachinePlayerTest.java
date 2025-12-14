package edu.univalle.battleship.model;

import static edu.univalle.battleship.model.Orientation.VERTICAL;
import static org.junit.jupiter.api.Assertions.*;
import edu.univalle.battleship.model.*;
import org.junit.jupiter.api.Test;

class MachinePlayerTest {

    @Test
    void returnsBoardCorrectly(){
        MachinePlayer machinePlayer = new MachinePlayer();

        assertNotNull(machinePlayer.getBoard());

    }
    @Test
    void returnsFleetCorrectly(){
        MachinePlayer machinePlayer = new MachinePlayer();
        assertNotNull(machinePlayer.getFleet());

    }

    @Test
    void machineShootsCorrectly(){
        MachinePlayer machinePlayer = new MachinePlayer();
        Player player = new Player();
        Board board = player.getBoard();
        Ship ship = new Ship("Plane",1);
        ship.place(5,4,VERTICAL);
        board.placeShip(ship);
        assertNotNull(machinePlayer.shoot(player));
    }


}