package edu.univalle.battleship.controller;

import edu.univalle.battleship.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.io.InputStream;
import java.util.Map;

/**
 * Controller for the ship placement scene.
 * <p>
 * Allows the player to place ships on their board, rotate them, and start the game.
 * Handles drag-and-drop of ships from the fleet box onto the board.
 */
public class PositionController {

    @FXML
    private GridPane playerBoard;

    @FXML
    private HBox fleetBox;

    @FXML
    private Button btnOrientation;

    @FXML
    public Button btnStartGame;

    private boolean horizontal = false;
    private int shipsPlaced = 0;
    private final int totalShipsToPlace = 10;

    private Player player;

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

    /**
     * Sets the current human player.
     *
     * @param player the player object
     */
    public void setPlayer(Player player) { this.player = player; }

    /**
     * Returns the GridPane representing the player's board.
     *
     * @return playerBoard GridPane
     */
    public GridPane getPlayerBoard() { return playerBoard; }

    /**
     * JavaFX initialization method.
     * Sets up the board grid, drag-and-drop listeners, and the orientation button.
     */
    @FXML
    public void initialize() {
        btnStartGame.setVisible(false);

        btnOrientation.setOnAction(e -> {
            horizontal = !horizontal;
            btnOrientation.setText(horizontal ? "Horizontal" : "Vertical");
        });

        setupGrid();
        renderBoard();

        GameManager.getInstance().setPositionController(this);
    }

    /**
     * Prepares the controller for a new game.
     * Loads the player's fleet and resets counters.
     *
     * @param player the player to use for the game
     */
    public void setupForNewGame(Player player) {
        this.player = player;
        shipsPlaced = 0;
        btnStartGame.setVisible(false);
        loadFleet();
    }

    /**
     * Builds the board grid with 40x40 rectangles and configures drag-over and drop events.
     */
    private void setupGrid() {
        for (int i = 0; i < Board.SIZE; i++) {
            ColumnConstraints col = new ColumnConstraints(40);
            col.setHgrow(Priority.NEVER);
            RowConstraints row = new RowConstraints(40);
            row.setVgrow(Priority.NEVER);
            playerBoard.getColumnConstraints().add(col);
            playerBoard.getRowConstraints().add(row);
        }
    }

    /**
     * Renders the empty board and adds listeners for drag-and-drop events.
     */
    private void renderBoard() {
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                StackPane cell = new StackPane();
                Rectangle rect = new Rectangle(40, 40);
                rect.setFill(Color.LIGHTBLUE);
                rect.setStroke(Color.BLACK);
                cell.getChildren().add(rect);

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

    /**
     * Loads all ships into the fleet box according to shipLimits.
     */
    private void loadFleet() {
        for (String fileName : shipLimits.keySet()) {
            int copies = shipLimits.get(fileName);
            for (int i = 0; i < copies; i++) addShip(fileName);
        }
    }

    /**
     * Adds a single ship ImageView to the fleet box and sets up drag detection.
     *
     * @param fileName the image file name of the ship
     */
    private void addShip(String fileName) {
        Image img = new Image(getClass().getResourceAsStream("/edu/univalle/battleship/images/" + fileName));
        int shipSize = shipSizes.get(fileName);

        ImageView view = new ImageView(img);
        view.setUserData(fileName);
        view.setFitWidth(40);
        view.setFitHeight(shipSize * 40);
        view.setPreserveRatio(false);

        view.setOnDragDetected(event -> {
            Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(fileName);
            db.setContent(content);
            event.consume();
        });

        fleetBox.getChildren().add(view);
    }

    /**
     * Handles a ship being dropped onto a board cell.
     * Validates placement, updates the board, and registers the ship with the player.
     *
     * @param event the drag event
     * @param cell  the target board cell
     */
    private void handleDrop(DragEvent event, StackPane cell) {
        if (player == null) return; // 🔹 proteger null

        Dragboard db = event.getDragboard();
        if (!db.hasString()) return;

        String shipName = db.getString();
        int shipSize = shipSizes.get(shipName);

        int col = GridPane.getColumnIndex(cell);
        int row = GridPane.getRowIndex(cell);

        // Validación de límites
        if ((horizontal && col + shipSize > Board.SIZE) || (!horizontal && row + shipSize > Board.SIZE)) return;

        int[][] boardCells = player.getBoard().getCells();
        for (int i = 0; i < shipSize; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (boardCells[r][c] == 1) return; // ya hay barco
        }

        // Crear ImageView del barco
        Image img = new Image(getClass().getResourceAsStream("/edu/univalle/battleship/images/" + shipName));
        ImageView shipView = new ImageView(img);
        shipView.setUserData(shipName);
        shipView.setFitWidth(40);
        shipView.setFitHeight(shipSize * 40);
        if (horizontal) {
            shipView.setRotate(-90);
            shipView.setTranslateX((shipSize - 1) * 20.0);
        }

        GridPane.setColumnIndex(shipView, col);
        GridPane.setRowIndex(shipView, row);
        GridPane.setColumnSpan(shipView, horizontal ? shipSize : 1);
        GridPane.setRowSpan(shipView, horizontal ? 1 : shipSize);
        playerBoard.getChildren().add(shipView);

        // Actualizar board
        for (int i = 0; i < shipSize; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            boardCells[r][c] = 1;
        }

        // Registrar el barco en player
        Orientation orient = horizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        Ship ship = new Ship(shipName, shipSize);
        ship.place(row, col, orient);
        player.addShip(ship);

        Node source = (Node) event.getGestureSource();
        fleetBox.getChildren().remove(source);
        shipsPlaced++;

        event.setDropCompleted(true);
        event.consume();

        if (shipsPlaced >= totalShipsToPlace) btnStartGame.setVisible(true);
    }

    /**
     * Handles starting the game once all ships are placed.
     * Collects all ship positions, registers them in the player, and launches the opponent board.
     */
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
                    int row = GridPane.getRowIndex(shipView);
                    int col = GridPane.getColumnIndex(shipView);
                    boolean horizontalPlacement = (shipView.getRotate() == -90);
                    Orientation orient = horizontalPlacement ? Orientation.HORIZONTAL : Orientation.VERTICAL;

                    Ship ship = new Ship(fileName, shipSize);
                    ship.place(row, col, orient);
                    human.addShip(ship);
                    human.getBoard().placeShip(ship);
                }
            }

            GameManager.getInstance().startNewGame(human);
            GameManager.getInstance().setPlayerBoardGrid(playerBoard);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/univalle/battleship/enemyPreviewView.fxml"));
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

    /**
     * Adds an image to a given StackPane cell.
     *
     * @param cell the cell to add the image to
     * @param path the path of the image resource
     */
    private void addImageToCell(StackPane cell, String path) {
        Image img = loadImage(path);
        if (img == null) return;
        ImageView iv = new ImageView(img);
        iv.setFitWidth(40);
        iv.setFitHeight(40);
        cell.getChildren().add(iv);
    }

    /**
     * Safely loads an image from the given resource path.
     *
     * @param path the image path
     * @return Image object or null if not found
     */
    private Image loadImage(String path) {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            System.err.println("No se encontró la imagen: " + path);
            return null;
        }
        return new Image(is);
    }

    /**
     * Returns the appropriate image filename for a ship name.
     *
     * @param shipName the ship name
     * @return the corresponding image filename
     */
    private String getShipImageName(String shipName) {
        shipName = shipName.toLowerCase();
        if (shipName.contains("carrier")) return "carrier.png";
        if (shipName.contains("submarine")) return "submarine.png";
        if (shipName.contains("destroyer")) return "destroyer.png";
        return "plane.png";
    }

    /**
     * Returns the StackPane node in the GridPane at the given row and column.
     *
     * @param grid the GridPane
     * @param row  the row index
     * @param col  the column index
     * @return the StackPane at the given position or null if not found
     */
    private StackPane getNodeFromGridPane(GridPane grid, int row, int col) {
        for (Node node : grid.getChildren()) {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);
            if (r == null) r = 0;
            if (c == null) c = 0;
            if (r == row && c == col && node instanceof StackPane cell) return cell;
        }
        return null;
    }

    /**
     * Rebuilds the player's board by drawing all ships currently registered in the player object.
     */
    public void rebuildPlayerBoard() {
        if (player == null) return;

        for (Node node : playerBoard.getChildren()) {
            if (node instanceof StackPane cell) {
                cell.getChildren().removeIf(n -> n instanceof ImageView);
            }
        }

        //Dibujar barcos
        for (Ship ship : player.getFleet()) {
            int[][] pos = ship.getPositions();
            int size = pos.length;
            boolean horizontalShip = size > 1 && pos[0][0] == pos[1][0];

            String imgName = getShipImageName(ship.getName());
            Image img = loadImage("/edu/univalle/battleship/images/" + imgName);
            if (img == null) continue;

            ImageView view = new ImageView(img);
            view.setMouseTransparent(true);
            view.setFitWidth(40);
            view.setFitHeight(40 * size);

            if (horizontalShip) {
                view.setRotate(-90);
                view.setTranslateX((size - 1) * 20.0);
            }

            GridPane.setRowIndex(view, pos[0][0]);
            GridPane.setColumnIndex(view, pos[0][1]);
            if (horizontalShip) GridPane.setColumnSpan(view, size);
            else GridPane.setRowSpan(view, size);

            playerBoard.getChildren().add(view);
        }
    }

    /**
     * Rebuilds only the shots (hit, miss, sunk) on the player's board, leaving ships intact.
     */
    public void rebuildPlayerShotsOnly() {
        if (player == null) return;

        int[][] cells = player.getBoard().getCells();

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {

                StackPane cell = getNodeFromGridPane(playerBoard, r, c);
                if (cell == null) continue;

                // 🔹 quitar SOLO imágenes
                cell.getChildren().removeIf(n -> n instanceof ImageView);

                switch (cells[r][c]) {
                    case 2 -> {
                        cell.getChildren().removeIf(n -> n instanceof ImageView);
                        addImageToCell(cell, "/edu/univalle/battleship/images/hit.png");
                    }
                    case 3 -> {
                        cell.getChildren().removeIf(n -> n instanceof ImageView);
                        addImageToCell(cell, "/edu/univalle/battleship/images/sink.png");
                    }
                    case 4 -> {
                        cell.getChildren().removeIf(n -> n instanceof ImageView);
                        addImageToCell(cell, "/edu/univalle/battleship/images/miss.png");
                    }
                }

            }
        }
    }


}
