package edu.univalle.battleship.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameStateHandler {

    private static final String SAVE_FILE = "savegame.dat";

    public static class ShipState implements Serializable {
        private String name;
        private int size;
        private int row, col;
        private Orientation orientation;
        private boolean[] hits;

        public ShipState(Ship ship) {
            name = ship.getName();
            size = ship.getSize();
            row = ship.getRow();
            col = ship.getColumn();
            orientation = ship.getOrientation();
            hits = ship.getHitsArray();
        }



        public Ship toShip() {
            Ship ship = new Ship(name, size);
            ship.place(row, col, orientation);
            ship.restoreHits(hits);
            return ship;
        }

    }

    public static class PlayerState implements Serializable {
        private List<ShipState> fleet;
        private int[][] boardCells;

        public PlayerState(Player player) {
            fleet = new ArrayList<>();
            for (Ship s : player.getFleet()) fleet.add(new ShipState(s));
            boardCells = player.getBoard().getCells();
        }

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

    public static class GameState implements Serializable {
        private PlayerState playerState;
        private PlayerState machineState;

        public GameState(Player player, MachinePlayer machine) {
            this.playerState = new PlayerState(player);
            this.machineState = new PlayerState(machineToPlayer(machine));
        }

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

    public static void saveGame(Player player, MachinePlayer machine) {
        GameState state = new GameState(player, machine);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        return state != null ? state.getPlayerState().toPlayer() : null;
    }

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
