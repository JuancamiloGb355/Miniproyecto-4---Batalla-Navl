package edu.univalle.battleship.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class responsible for saving and loading the game state.
 * <p>
 * This class serializes the state of the human player and the machine
 * player, allowing the game to be resumed later.
 */
public class GameStateHandler {

    /**
     * File used to store the serialized game state.
     */
    private static final String SAVE_FILE = "savegame.dat";

    /**
     * Serializable snapshot of a Ship.
     * <p>
     * Stores only the necessary information to reconstruct a Ship
     * instance after loading.
     */
    public static class ShipState implements Serializable {

        private String name;
        private int size;
        private int row, col;
        private Orientation orientation;
        private boolean[] hits;

        /**
         * Creates a ShipState from an existing Ship.
         *
         * @param ship the ship to serialize
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
         * Reconstructs a Ship instance from this saved state.
         *
         * @return a restored Ship object
         */
        public Ship toShip() {
            Ship ship = new Ship(name, size);
            ship.place(row, col, orientation);
            ship.restoreHits(hits);
            return ship;
        }
    }

    /**
     * Serializable snapshot of a Player.
     * <p>
     * Contains the player's fleet and the state of the board cells.
     */
    public static class PlayerState implements Serializable {

        private List<ShipState> fleet;
        private int[][] boardCells;

        /**
         * Creates a PlayerState from an existing Player.
         *
         * @param player the player to serialize
         */
        public PlayerState(Player player) {
            fleet = new ArrayList<>();
            for (Ship s : player.getFleet()) {
                fleet.add(new ShipState(s));
            }
            boardCells = player.getBoard().getCells();
        }

        /**
         * Reconstructs a Player instance from this saved state.
         *
         * @return a restored Player object
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
     * Serializable snapshot of the entire game state.
     * <p>
     * Stores both the human player state and the machine player state.
     */
    public static class GameState implements Serializable {

        private PlayerState playerState;
        private PlayerState machineState;

        /**
         * Creates a GameState from the current players.
         *
         * @param player the human player
         * @param machine the machine player
         */
        public GameState(Player player, MachinePlayer machine) {
            this.playerState = new PlayerState(player);
            this.machineState = new PlayerState(machineToPlayer(machine));
        }

        /**
         * Converts a MachinePlayer into a temporary Player instance
         * to reuse the PlayerState serialization logic.
         *
         * @param m the machine player
         * @return a temporary Player representation of the machine
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

        /**
         * Returns the saved human player state.
         *
         * @return the player state
         */
        public PlayerState getPlayerState() {
            return playerState;
        }

        /**
         * Returns the saved machine player state.
         *
         * @return the machine player state
         */
        public PlayerState getMachineState() {
            return machineState;
        }
    }

    /**
     * Saves the current game state to disk.
     *
     * @param player the human player
     * @param machine the machine player
     */
    public static void saveGame(Player player, MachinePlayer machine) {
        GameState state = new GameState(player, machine);
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the complete game state from disk.
     *
     * @return the loaded GameState, or {@code null} if loading fails
     */
    public static GameState loadGame() {
        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads and reconstructs the human player from the saved game.
     *
     * @return the restored human player, or {@code null} if no save exists
     */
    public static Player loadPlayer() {
        GameState state = loadGame();
        return state != null ? state.getPlayerState().toPlayer() : null;
    }

    /**
     * Loads and reconstructs the machine player from the saved game.
     *
     * @return the restored machine player, or {@code null} if no save exists
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
