package edu.univalle.battleship.controller;

import edu.univalle.battleship.model.*;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class OpponentController {

    @FXML
    private GridPane opponentBoard;

    private MachinePlayer machine;
    private Player human;

    @FXML
    public void initialize() {
        machine = new MachinePlayer();
        machine.placeFleetAutomatically(); // coloca la flota de la máquina

        createBoard();

        // Espera a que el GridPane tenga una Scene
        opponentBoard.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.V) {
                        revealShips();
                    }
                });
            }
        });
    }


    // Metodo para mostrar todos los barcos en el GridPane
    private void revealShips() {
        for (Ship ship : machine.getFleet()) {
            String imgName;
            if (ship.getName().toLowerCase().contains("carrier")) {
                imgName = "carrier.png";
            } else if (ship.getName().toLowerCase().contains("submarine")) {
                imgName = "submarine.png";
            } else if (ship.getName().toLowerCase().contains("destroyer")) {
                imgName = "destroyer.png";
            } else { // patrol
                imgName = "plane.png";
            }

            for (int[] pos : ship.getPositions()) {
                StackPane cell = getNodeFromGridPane(opponentBoard, pos[0], pos[1]);
                if (cell != null) {
                    ImageView shipImg = new ImageView(
                            new Image(getClass().getResourceAsStream(
                                    "/edu/univalle/battleship/images/" + imgName)));
                    shipImg.setFitWidth(40);
                    shipImg.setFitHeight(40);
                    cell.getChildren().add(shipImg);
                }
            }
        }
    }



    private void createBoard() {
        int size = Board.SIZE; // supongamos 10

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(40, 40);
                cell.setStyle("-fx-border-color: white; -fx-background-color: #87CEFA;");

                final int r = row;
                final int c = col;

                cell.setOnMouseClicked(event -> handleShot(r, c, cell));

                opponentBoard.add(cell, col, row);
            }
        }
    }

    private void handleShot(int row, int col, StackPane cell) {

        Board board = machine.getBoard();

        // Validar si ya dispararon
        if (board.isShotRepeated(row, col)) {
            System.out.println("Already shot here!");
            return;
        }

        // Hacer el disparo
        String result = board.receiveShot(row, col);

        switch (result) {
            case "hit":
                ImageView hit = new ImageView(new Image(getClass().getResourceAsStream(
                        "/edu/univalle/battleship/images/hit.png")));
                hit.setFitWidth(40);
                hit.setFitHeight(40);
                cell.getChildren().add(hit);
                System.out.println("Hit! You can shoot again.");
                break;

            case "miss":
                ImageView miss = new ImageView(new Image(getClass().getResourceAsStream(
                        "/edu/univalle/battleship/images/miss.png")));
                miss.setFitWidth(40);
                miss.setFitHeight(40);
                cell.getChildren().add(miss);
                System.out.println("Miss! Turn ends.");
                // Aquí puedes agregar el turno de la máquina si quieres
                break;

            default: // sunk:NombreDelBarco
                if (result.startsWith("sunk:")) {
                    String sunkShipName = result.split(":")[1];
                    Ship sunkShip = null;
                    for (Ship s : machine.getFleet()) {
                        if (s.getName().equals(sunkShipName)) {
                            sunkShip = s;
                            break;
                        }
                    }

                    if (sunkShip != null) {
                        // Actualizar todas las posiciones del barco a sink.png
                        for (int[] pos : sunkShip.getPositions()) {
                            StackPane shipCell = getNodeFromGridPane(opponentBoard, pos[0], pos[1]);
                            if (shipCell != null) {
                                shipCell.getChildren().clear();
                                ImageView sink = new ImageView(new Image(
                                        getClass().getResourceAsStream("/edu/univalle/battleship/images/sink.png")));
                                sink.setFitWidth(40);
                                sink.setFitHeight(40);
                                shipCell.getChildren().add(sink);
                            }
                        }
                    }
                    System.out.println("Sunk " + sunkShipName + "! You can shoot again.");
                }
                break;
        }
    }


    /** Busca un StackPane en el GridPane según fila y columna */
    private StackPane getNodeFromGridPane(GridPane grid, int row, int col) {
        for (var node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (StackPane) node;
            }
        }
        return null;
    }

    /** Agrega una imagen a un StackPane */
    private void addImageToCell(StackPane cell, String imagePath) {
        ImageView img = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        img.setFitWidth(40);
        img.setFitHeight(40);
        cell.getChildren().add(img);
    }

}
