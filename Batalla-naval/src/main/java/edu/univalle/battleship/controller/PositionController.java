package edu.univalle.battleship.controller;

import edu.univalle.battleship.model.*;
import edu.univalle.battleship.model.planeTextFiles.PlaneTextFileHandler;
import javafx.application.Platform;
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
    public Button btnStartGame;

    private int totalShipsToPlace = 10;
    private int shipsPlaced = 0;

    private Player player;

    public GridPane getPlayerBoard() {
        return playerBoard;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void rebuildPlayerBoard() {
        if (player == null) return;

        // Limpiar tablero visual pero mantener celdas
        for (Node node : playerBoard.getChildren()) {
            if (node instanceof StackPane cell) {
                cell.getChildren().removeIf(child -> child instanceof ImageView);
            }
        }

        // Dibujar barcos
        for (Ship ship : player.getFleet()) {
            int[][] positions = ship.getPositions();
            int shipSize = positions.length;
            boolean horizontal = positions.length > 1 && positions[0][0] == positions[1][0];

            String name = ship.getName().toLowerCase();
            String imgName;

            if (name.contains("carrier")) {
                imgName = "carrier.png";
            } else if (name.contains("submarine")) {
                imgName = "submarine.png";
            } else if (name.contains("destroyer")) {
                imgName = "destroyer.png";
            } else {
                imgName = "plane.png"; // patrol
            }


            Image img = new Image(getClass().getResourceAsStream("/edu/univalle/battleship/images/" + imgName));
            ImageView shipView = new ImageView(img);
            shipView.setUserData(imgName);
            shipView.setMouseTransparent(true);

            if (horizontal) {
                shipView.setRotate(-90);
                shipView.setFitWidth(40);
                shipView.setFitHeight(40 * shipSize);
                double offset = (shipSize - 1) * 20.0;
                shipView.setTranslateX(offset);
            } else {
                shipView.setFitWidth(40);
                shipView.setFitHeight(40 * shipSize);
            }

            // Ubicar en la primera celda
            StackPane cell = getNodeFromGridPane(playerBoard, positions[0][0], positions[0][1]);
            if (cell != null) {
                GridPane.setRowIndex(shipView, positions[0][0]);
                GridPane.setColumnIndex(shipView, positions[0][1]);
                if (horizontal) GridPane.setColumnSpan(shipView, shipSize);
                else GridPane.setRowSpan(shipView, shipSize);
                playerBoard.getChildren().add(shipView);
            }
        }

        // Dibujar disparos previos (hits y misses)
        boolean[][] hits = player.getBoard().getHits();
        boolean[][] misses = player.getBoard().getMisses();
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                StackPane cell = getNodeFromGridPane(playerBoard, r, c);
                if (cell == null) continue;
                if (hits[r][c]) addImageToCell(cell, "/edu/univalle/battleship/images/hit.png");
                else if (misses[r][c]) addImageToCell(cell, "/edu/univalle/battleship/images/miss.png");
            }
        }
    }

    private void addImageToCell(StackPane cell, String imgPath) {
        if (cell == null) return;

        Image img = new Image(getClass().getResourceAsStream(imgPath));
        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(40);
        imgView.setFitHeight(40);
        cell.getChildren().add(imgView);
    }


    private StackPane getNodeFromGridPane(GridPane grid, int row, int col) {
        for (Node node : grid.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer colIndex = GridPane.getColumnIndex(node);
            if (rowIndex == null) rowIndex = 0;
            if (colIndex == null) colIndex = 0;

            if (rowIndex == row && colIndex == col && node instanceof StackPane cell) {
                return cell;
            }
        }
        return null;
    }

    /**
     * Actualiza la celda visual como golpe acertado (hit)
     */
    public void updateCellAsHit(int row, int col) {
        // Obtener la celda del GridPane (suponiendo que cada celda es un Button o Pane)
        javafx.scene.Node node = getNodeFromGrid(playerBoard, row, col);
        if (node != null) {
            node.setStyle("-fx-background-color: red; -fx-border-color: black;");
        }
    }

    /**
     * Actualiza la celda visual como disparo fallido (miss)
     */
    public void updateCellAsMiss(int row, int col) {
        javafx.scene.Node node = getNodeFromGrid(playerBoard, row, col);
        if (node != null) {
            node.setStyle("-fx-background-color: lightblue; -fx-border-color: black;");
        }
    }

    /**
     * Método auxiliar para obtener el nodo (celda) en el GridPane
     */
    private javafx.scene.Node getNodeFromGrid(GridPane grid, int row, int col) {
        for (javafx.scene.Node node : grid.getChildren()) {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);
            if (r != null && c != null && r == row && c == col) {
                return node;
            }
        }
        return null;
    }

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

        javafx.application.Platform.runLater(() -> {
            playerBoard.getScene().setUserData(this);
        });

        GameManager.getInstance().setPositionController(this);

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

    public void clearShotsOnly() {
        for (var node : playerBoard.getChildren()) {
            if (node instanceof StackPane cell) {
                if (cell.getChildren().size() > 1) {
                    // Mantiene el barco y elimina hit/miss/sink
                    cell.getChildren().remove(1, cell.getChildren().size());
                }
            }
        }
    }


    @FXML
    private void handleStartGame() {
        try {

            btnStartGame.setDisable(true);

            Player human = new Player();

            for (Node node : playerBoard.getChildren()) {
                if (node instanceof ImageView shipView) {

                    String fileName = (String) shipView.getUserData();
                    if (fileName == null) continue;

                    int shipSize = shipSizes.get(fileName);

                    int col = GridPane.getColumnIndex(shipView);
                    int row = GridPane.getRowIndex(shipView);

                    boolean horizontalPlacement = (shipView.getRotate() == -90);

                    Orientation orient = horizontalPlacement
                            ? Orientation.HORIZONTAL
                            : Orientation.VERTICAL;

                    Ship ship = new Ship(fileName, shipSize);
                    ship.place(row, col, orient);

                    human.addShip(ship);
                    human.getBoard().placeShip(ship);
                }
            }

            GameManager.getInstance().startNewGame(human);

            System.out.println("HUMAN BOARD ships: "
                    + GameManager.getInstance().getHuman().getBoard().getShips().size());
            System.out.println("HUMAN FLEET ships: "
                    + GameManager.getInstance().getHuman().getFleet().size());
            System.out.println("MACHINE BOARD ships: "
                    + GameManager.getInstance().getMachine().getBoard().getShips().size());
            System.out.println("MACHINE FLEET ships: "
                    + GameManager.getInstance().getMachine().getFleet().size());



            System.out.println(
                    "TEST → Barcos del jugador registrados en GameManager: "
                            + GameManager.getInstance().getHuman().getFleet().size()
            );

            System.out.println(
                    "TEST → Barcos de la máquina: "
                            + GameManager.getInstance().getMachine().getFleet().size()
            );

            GameManager.getInstance().setPlayerBoardGrid(playerBoard);


            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/univalle/battleship/enemyPreviewView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Opponent Board");
            stage.setScene(new Scene(root));
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
            btnStartGame.setDisable(false);
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

        view.setUserData(fileName);

        // REAL SIZE (matching game board)
        view.setFitWidth(40);
        view.setFitHeight(shipSize * 40);
        view.setPreserveRatio(false);

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
        shipView.setUserData(shipName);

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
