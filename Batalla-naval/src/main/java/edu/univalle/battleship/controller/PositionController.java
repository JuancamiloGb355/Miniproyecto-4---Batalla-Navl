package edu.univalle.battleship.controller;

import edu.univalle.battleship.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class PositionController {

    @FXML private GridPane gridBoard;
    @FXML private Button btnOrientation;

    private Orientation currentOrientation = Orientation.HORIZONTAL;
    private Board playerBoard;
    private List<Ship> shipsToPlace;
    private int currentShipIndex = 0;

    @FXML
    public void initialize() {
        playerBoard = new Board();
        shipsToPlace = createFleet();

        initGrid();
        initEvents();
        initOrientationButton();
    }

    private void initOrientationButton() {
        btnOrientation.setOnAction(e -> {
            currentOrientation = (currentOrientation == Orientation.HORIZONTAL)
                    ? Orientation.VERTICAL
                    : Orientation.HORIZONTAL;

            btnOrientation.setText(
                    currentOrientation == Orientation.HORIZONTAL ?
                            "Horizontal" : "Vertical"
            );
        });
    }

    private List<Ship> createFleet() {
        List<Ship> fleet = new ArrayList<>();
        fleet.add(new Ship("Carrier", 4));
        fleet.add(new Ship("Submarine 1", 3));
        fleet.add(new Ship("Submarine 2", 3));
        fleet.add(new Ship("Destroyer 1", 2));
        fleet.add(new Ship("Destroyer 2", 2));
        fleet.add(new Ship("Destroyer 3", 2));
        fleet.add(new Ship("Patrol Boat 1", 1));
        fleet.add(new Ship("Patrol Boat 2", 1));
        fleet.add(new Ship("Patrol Boat 3", 1));
        fleet.add(new Ship("Patrol Boat 4", 1));
        return fleet;
    }

    private void initGrid() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(40, 40);
                cell.setStyle("-fx-background-color: #BEEBFF;");
                gridBoard.add(cell, col, row);
            }
        }
    }

    private void initEvents() {
        for (var node : gridBoard.getChildren()) {
            StackPane cell = (StackPane) node;

            cell.setOnMouseClicked(event -> {
                int row = GridPane.getRowIndex(cell);
                int col = GridPane.getColumnIndex(cell);

                handlePlacement(row, col);
            });
        }
    }

    private void handlePlacement(int row, int col) {
        if (currentShipIndex >= shipsToPlace.size()) return;

        Ship ship = shipsToPlace.get(currentShipIndex);

        if (!playerBoard.canPlace(ship, row, col, currentOrientation)) {
            System.out.println("Invalid placement.");
            return;
        }

        ship.place(row, col, currentOrientation);
        playerBoard.placeShip(ship);
        paintShip(ship);

        currentShipIndex++;

        if (currentShipIndex == shipsToPlace.size()) {
            System.out.println("All ships placed successfully.");
        }
    }

    private void paintShip(Ship ship) {
        int row = ship.getRow();
        int col = ship.getColumn();
        int size = ship.getSize();

        int dx = (ship.getOrientation() == Orientation.VERTICAL) ? 1 : 0;
        int dy = (ship.getOrientation() == Orientation.HORIZONTAL) ? 1 : 0;

        for (int i = 0; i < size; i++) {
            StackPane cell = getCell(row + i * dx, col + i * dy);
            cell.setStyle("-fx-background-color: #2A6FB3;");
        }
    }

    private StackPane getCell(int row, int col) {
        for (var node : gridBoard.getChildren()) {
            if (GridPane.getRowIndex(node) == row &&
                    GridPane.getColumnIndex(node) == col) {
                return (StackPane) node;
            }
        }
        return null;
    }
}
