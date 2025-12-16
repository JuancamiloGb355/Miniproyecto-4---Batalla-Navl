package edu.univalle.battleship.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading the game state to a file.
 * <p>
 * Provides serialization of players, machines, and ships, allowing
 * the game to be saved and restored later.
 */
public class GameStateHandler {

    private static final String SAVE_FILE = "savegame.dat";

    /**
     * Represents the state of a single ship for serialization.
     */
    public static class ShipState implements Serializable {
        private String name;
        private int size;
        private int row, col;
        private Orientation orientation;
        private boolean[] hits;

        /**
         * Constructs a ShipState from a given Ship.
         *
         * @param ship the ship to save
         */
        public ShipState(Ship ship) {
            name = ship.getName();
            size = ship.getSize();
            row = ship.getRow();
            col = ship.getColumn();
            orientation = ship.getOrientation();
            hits = ship.getHitsArray();
        }

        /**
         * Restores the Ship object from this saved state.
         *
         * @return a new Ship object with restored position and hits
         */
        public Ship toShip() {
            Ship ship = new Ship(name, size);
            ship.place(row, col, orientation);
            ship.restoreHits(hits);
            return ship;
        }
    }

    /**
     * Represents the state of a player for serialization.
     * Stores the fleet and board cells.
     */
    public static class PlayerState implements Serializable {
        private List<ShipState> fleet;
        private int[][] boardCells;

        /**
         * Constructs a PlayerState from a Player object.
         *
         * @param player the player to save
         */
        public PlayerState(Player player) {
            fleet = new ArrayList<>();
            for (Ship s : player.getFleet()) fleet.add(new ShipState(s));
            boardCells = player.getBoard().getCells();
        }

        /**
         * Restores the Player object from this saved state.
         *
         * @return a new Player object with restored fleet and board
         */
        public Player toPlayer() {
            Player player = new Player();
            for (ShipState s : fleet) {
                Ship ship = s.toShip();
                player.addShip(ship);
                player.getBoard().placeShip(ship);
            }
            player.getBoard().setCells(boardCells);
            return player;
        }
    }

    /**
     * Represents the full game state for serialization.
     * Contains the state of the human player and the machine.
     */
    public static class GameState implements Serializable {
        private PlayerState playerState;
        private PlayerState machineState;

        /**
         * Constructs a GameState from the human player and machine player.
         *
         * @param player  the human player
         * @param machine the machine player
         */
        public GameState(Player player, MachinePlayer machine) {
            this.playerState = new PlayerState(player);
            this.machineState = new PlayerState(machineToPlayer(machine));
        }

        /**
         * Converts a MachinePlayer to a Player object for saving.
         *
         * @param m the machine player
         * @return a Player object representing the machine
         */
        private Player machineToPlayer(MachinePlayer m) {
            Player temp = new Player();
            for (Ship s : m.getFleet()) {
                temp.addShip(s);
                temp.getBoard().placeShip(s);
            }
            temp.getBoard().setCells(m.getBoard().getCells());
            return temp;
        }

        public PlayerState getPlayerState() { return playerState; }
        public PlayerState getMachineState() { return machineState; }
    }

    /**
     * Saves the current game state to a file.
     *
     * @param player  the human player
     * @param machine the machine player
     */
    public static void saveGame(Player player, MachinePlayer machine) {
        GameState state = new GameState(player, machine);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the game state from the save file.
     *
     * @return the GameState object, or null if an error occurs
     */
    public static GameState loadGame() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads the human player from the saved game.
     *
     * @return the restored Player object, or null if not found
     */
    public static Player loadPlayer() {
        GameState state = loadGame();
        return state != null ? state.getPlayerState().toPlayer() : null;
    }

    /**
     * Loads the machine player from the saved game.
     *
     * @return the restored MachinePlayer object, or null if not found
     */
    public static MachinePlayer loadMachine() {
        GameState state = loadGame();
        if (state != null) {
            Player temp = state.getMachineState().toPlayer();
            MachinePlayer m = new MachinePlayer();
            for (Ship s : temp.getFleet()) {
                m.getFleet().add(s);
                m.getBoard().placeShip(s);
            }
            m.getBoard().setCells(temp.getBoard().getCells());
            return m;
        }
        return null;
    }
}
