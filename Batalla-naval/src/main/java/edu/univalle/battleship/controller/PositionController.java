package edu.univalle.battleship.controller;

import edu.univalle.battleship.model.planeTextFiles.PlaneTextFileHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class PositionController {

    PlaneTextFileHandler planeTextFileHandler;

    @FXML
    private void handleStartGame() {
        try {


            // Cargar el FXML del tablero del enemigo
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/univalle/battleship/enemyPreviewView.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Opponent Board");
            stage.setScene(new Scene(root));
            stage.show();
            OpponentController controller = (OpponentController) loader.getController();
            String content = Integer.toString(controller.getNumberofsunkenships());
            planeTextFileHandler = new PlaneTextFileHandler();
            planeTextFileHandler.write("player_data.csv", content);


            // Opcional: cerrar la ventana de colocación de flota
            // ((Stage) btnStartGame.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private Button btnStartGame;

    private int totalShipsToPlace = 10;
    private int shipsPlaced = 0;


    private boolean horizontal = false;
    private final int[][] board = new int[10][10]; // 0 = empty, 1 = occupied

    private final Map<String, Integer> shipSizes = Map.of(
            "carrier.png", 4,
            "submarine.png", 3,
            "destroyer.png", 2,
            "plane.png", 1
    );

    private final Map<String, Integer> shipLimits = Map.of(
            "carrier.png", 1,
            "submarine.png", 2,
            "destroyer.png", 3,
            "plane.png", 4
    );


    @FXML
    private GridPane playerBoard;

    @FXML
    private HBox fleetBox;

    @FXML
    private Button btnOrientation;

    @FXML
    public void initialize() {

        System.out.println("PositionController initialized");
        btnOrientation.setOnAction(e -> {
            horizontal = !horizontal;
            btnOrientation.setText(horizontal ? "Horizontal" : "Vertical");
        });

        setupGrid();
        renderBoard();   // Create 10×10 board
        loadFleet();     // Add ships to bottom HBox
    }

    private void setupGrid() {
        for (int i = 0; i < 10; i++) {
            ColumnConstraints col = new ColumnConstraints(40); // ancho fijo
            col.setHgrow(Priority.NEVER);

            RowConstraints row = new RowConstraints(40); // alto fijo
            row.setVgrow(Priority.NEVER);

            playerBoard.getColumnConstraints().add(col);
            playerBoard.getRowConstraints().add(row);
        }
    }


     //------------------------------
     //CREATE GRID BOARD
     //------------------------------
    private void renderBoard() {
        int size = 10;
        int cellSize = 40;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {

                StackPane cell = new StackPane();
                Rectangle rect = new Rectangle(cellSize, cellSize);
                rect.setFill(Color.LIGHTBLUE);
                rect.setStroke(Color.BLACK);

                cell.getChildren().add(rect);

                // Accept drops
                cell.setOnDragOver(event -> {
                    if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }
                    event.consume();
                });

                cell.setOnDragDropped(event -> handleDrop(event, cell));

                playerBoard.add(cell, c, r);
            }
        }
    }

    //----------------------------
    // LOAD SHIPS IN THE FLEET BOX
    //------------------------------
    private void loadFleet() {

        for (String fileName : shipLimits.keySet()) {

            int copies = shipLimits.get(fileName);

            for (int i = 0; i < copies; i++) {
                addShip(fileName);
            }
        }
    }

    private void addShip(String fileName) {
        Image img = new Image(getClass().getResourceAsStream(
                "/edu/univalle/battleship/images/" + fileName
        ));

        int shipSize = shipSizes.get(fileName);

        ImageView view = new ImageView(img);

        // REAL SIZE (matching game board)
        view.setFitWidth(40);
        view.setFitHeight(shipSize * 40);
        view.setPreserveRatio(false); // important!

        // Start dragging
        view.setOnDragDetected(event -> {
            Dragboard db = view.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString(fileName);
            db.setContent(content);

            event.consume();
        });

        fleetBox.getChildren().add(view);
    }



     //------------------------------
     // HANDLE DROP ON GRID CELL
     //------------------------------
    private void handleDrop(DragEvent event, StackPane cell) {
        Dragboard db = event.getDragboard();
        if (!db.hasString()) return;

        String shipName = db.getString();
        int shipSize = shipSizes.get(shipName);

        int col = GridPane.getColumnIndex(cell);
        int row = GridPane.getRowIndex(cell);

        // ----------------------------------
        // Validate ship doesn't go out of bounds
        // ----------------------------------
        if (horizontal) {
            if (col + shipSize > 10) {
                System.out.println("Ship out of bounds!");
                return;
            }
        } else {
            if (row + shipSize > 10) {
                System.out.println("Ship out of bounds!");
                return;
            }
        }

        // ----------------------------------
        // Validate no overlap
        // ----------------------------------
        for (int i = 0; i < shipSize; i++) {
            int checkRow = row + (horizontal ? 0 : i);
            int checkCol = col + (horizontal ? i : 0);

            if (board[checkRow][checkCol] == 1) {
                System.out.println("Ship overlaps another ship!");
                return;
            }
        }

        // ----------------------------------
        // Place visually
        // ----------------------------------
        Image img = new Image(getClass().getResourceAsStream("/edu/univalle/battleship/images/" + shipName));
        ImageView shipView = new ImageView(img);

        if (horizontal) {
            shipView.setRotate(-90);

            shipView.setFitWidth(40);
            shipView.setFitHeight(shipSize * 40);

            // Calcular desplazamiento para alinear la punta después de rotar
            double offset = (shipSize - 1) * 20.0;
            shipView.setTranslateX(offset);
            shipView.setTranslateY(0);

        } else {
            shipView.setRotate(0);

            shipView.setFitWidth(40);
            shipView.setFitHeight(shipSize * 40);

            shipView.setTranslateX(0);
            shipView.setTranslateY(0);
        }

        // Place image on the clicked cell
        StackPane parentCell = cell;

        GridPane.setColumnSpan(shipView, horizontal ? shipSize : 1);
        GridPane.setRowSpan(shipView, horizontal ? 1 : shipSize);

        playerBoard.getChildren().add(shipView);
        GridPane.setColumnIndex(shipView, col);
        GridPane.setRowIndex(shipView, row);

        if (horizontal) {
            GridPane.setColumnSpan(shipView, shipSize);
        } else {
            GridPane.setRowSpan(shipView, shipSize);
        }

        // ----------------------------------
        // Mark board cells as occupied
        // ----------------------------------
        for (int i = 0; i < shipSize; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            board[r][c] = 1;
        }

        // ----------------------------------
        // Remove the ship in the fleet
        // ----------------------------------
        Node source = (Node) event.getGestureSource();
        fleetBox.getChildren().remove(source);
        shipsPlaced++;

        event.setDropCompleted(true);
        event.consume();

        System.out.println("Placed " + shipName + " at (" + row + "," + col + ")");

        // Mostrar botón Start Game si ya se colocaron todos los barcos
        if (shipsPlaced >= totalShipsToPlace) {
            btnStartGame.setVisible(true);
        }
    }

}
