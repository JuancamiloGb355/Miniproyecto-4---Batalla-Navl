package edu.univalle.battleship.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameStateHandler {

    private static final String SAVE_FILE = "savegame.dat";

    // ----------------------------------
    // Clase interna para serializar un barco
    // ----------------------------------
    public static class ShipState implements Serializable {
        private String name;
        private int size;
        private int row;
        private int column;
        private Orientation orientation;
        private int hits;

        public ShipState(Ship ship) {
            this.name = ship.getName();
            this.size = ship.getSize();
            this.row = ship.getRow();
            this.column = ship.getColumn();
            this.orientation = ship.getOrientation();
            this.hits = ship.getHits(); // número de hits que ha recibido
        }

        public Ship toShip() {
            Ship ship = new Ship(name, size);
            ship.place(row, column, orientation);
            for (int i = 0; i < hits; i++) {
                ship.hit();
            }
            return ship;
        }
    }

    // ----------------------------------
    // Clase interna para serializar un jugador (Player o Machine)
    // ----------------------------------
    public static class PlayerState implements Serializable {
        private List<ShipState> fleet;
        private boolean[][] hits;
        private boolean[][] misses;

        public PlayerState(Player player) {
            fleet = new ArrayList<>();
            for (Ship ship : player.getFleet()) {
                fleet.add(new ShipState(ship));
            }
            hits = player.getBoard().getHits();
            misses = player.getBoard().getMisses();
        }

        public Player toPlayer() {
            Player player = new Player();
            for (ShipState s : fleet) {
                Ship ship = s.toShip();
                player.addShip(ship);
                player.getBoard().placeShip(ship);
            }
            player.getBoard().setHits(hits);
            player.getBoard().setMisses(misses);
            return player;
        }
    }

    // ----------------------------------
    // Clase que guarda el estado completo del juego
    // ----------------------------------
    public static class GameState implements Serializable {
        private PlayerState playerState;
        private PlayerState machineState;

        public GameState(Player player, MachinePlayer machinePlayer) {
            this.playerState = new PlayerState(player);
            this.machineState = new PlayerState(machinePlayerToPlayer(machinePlayer));
        }

        private Player machinePlayerToPlayer(MachinePlayer machine) {
            Player temp = new Player();
            for (Ship s : machine.getFleet()) {
                temp.addShip(s);
                temp.getBoard().placeShip(s);
            }
            temp.getBoard().setHits(machine.getBoard().getHits());
            temp.getBoard().setMisses(machine.getBoard().getMisses());
            return temp;
        }

        public PlayerState getPlayerState() { return playerState; }
        public PlayerState getMachineState() { return machineState; }
    }

    // ----------------------------------
    // Guardar partida
    // ----------------------------------
    public static void saveGame(Player player, MachinePlayer machine) {
        GameState state = new GameState(player, machine);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------
    // Cargar partida completa
    // ----------------------------------
    public static GameState loadGame() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Player loadPlayer() {
        GameState state = loadGame();
        if (state != null) return state.getPlayerState().toPlayer();
        return null;
    }

    public static MachinePlayer loadMachine() {
        GameState state = loadGame();
        if (state != null) {
            Player temp = state.getMachineState().toPlayer();
            MachinePlayer machine = new MachinePlayer();
            for (Ship s : temp.getFleet()) {
                machine.getFleet().add(s);
                machine.getBoard().placeShip(s);
            }
            machine.getBoard().setHits(temp.getBoard().getHits());
            machine.getBoard().setMisses(temp.getBoard().getMisses());
            return machine;
        }
        return null;
    }
}
