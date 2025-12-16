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
 * Controller class for positioning the player's ships before starting the Battleship game.
 * Handles ship placement, drag-and-drop, orientation changes, and board rendering.
 */
public class PositionController {

    /** GridPane representing the player's board. */
    @FXML
    private GridPane playerBoard;

    /** HBox container holding the available ships for placement. */
    @FXML
    private HBox fleetBox;

    /** Button to toggle ship orientation between horizontal and vertical. */
    @FXML
    private Button btnOrientation;

    /** Button to start the game once all ships are placed. */
    @FXML
    public Button btnStartGame;

    /** Current ship orientation; true = horizontal, false = vertical. */
    private boolean horizontal = false;

    /** Counter for ships placed on the board. */
    private int shipsPlaced = 0;

    /** Total number of ships that must be placed before starting. */
    private final int totalShipsToPlace = 10;

    /** Player object representing the human player. */
    private Player player;

    /** Map storing ship sizes for each ship image file. */
    private final Map<String, Integer> shipSizes = Map.of(
            "carrier.png", 4,
            "submarine.png", 3,
            "destroyer.png", 2,
            "plane.png", 1
    );

    /** Map storing the maximum allowed copies of each ship type. */
    private final Map<String, Integer> shipLimits = Map.of(
            "carrier.png", 1,
            "submarine.png", 2,
            "destroyer.png", 3,
            "plane.png", 4
    );

    /**
     * Sets the player object for this controller.
     *
     * @param player The human player.
     */
    public void setPlayer(Player player) { this.player = player; }

    /**
     * Returns the GridPane representing the player's board.
     *
     * @return Player's board GridPane.
     */
    public GridPane getPlayerBoard() { return playerBoard; }

    /**
     * Initializes the controller, configures buttons, and sets up the board grid.
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
     * Prepares the controller for a new game with a given player.
     *
     * @param player The human player.
     */
    public void setupForNewGame(Player player) {
        this.player = player;
        shipsPlaced = 0;
        btnStartGame.setVisible(false);
        loadFleet();
    }

    /** Sets up the GridPane constraints for the board. */
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

    /** Renders the board with empty cells and configures drag-and-drop events. */
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

    /** Loads all ships into the fleetBox for placement on the board. */
    private void loadFleet() {
        for (String fileName : shipLimits.keySet()) {
            int copies = shipLimits.get(fileName);
            for (int i = 0; i < copies; i++) addShip(fileName);
        }
    }

    /**
     * Adds a single ship to the fleetBox with drag-and-drop enabled.
     *
     * @param fileName Image file representing the ship.
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
     * Handles the drop of a ship onto the board cell, updating the board state and UI.
     *
     * @param event The DragEvent triggered by dropping a ship.
     * @param cell  The target cell StackPane.
     */
    private void handleDrop(DragEvent event, StackPane cell) {
        if (player == null) return;

        Dragboard db = event.getDragboard();
        if (!db.hasString()) return;

        String shipName = db.getString();
        int shipSize = shipSizes.get(shipName);

        int col = GridPane.getColumnIndex(cell);
        int row = GridPane.getRowIndex(cell);

        // Validate placement boundaries
        if ((horizontal && col + shipSize > Board.SIZE) || (!horizontal && row + shipSize > Board.SIZE)) return;

        int[][] boardCells = player.getBoard().getCells();
        for (int i = 0; i < shipSize; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (boardCells[r][c] == 1) return; // Already occupied
        }

        // Create ImageView for the ship
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

        // Update board cells
        for (int i = 0; i < shipSize; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            boardCells[r][c] = 1;
        }

        // Register ship in player object
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

    /** Handles the start game button, creating a new player and loading the opponent view. */
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
     * Adds an image to a given cell safely.
     *
     * @param cell The target StackPane.
     * @param path Path to the image resource.
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
     * Safely loads an image from the resource path.
     *
     * @param path Path to the image resource.
     * @return The loaded Image object, or null if not found.
     */
    private Image loadImage(String path) {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            System.err.println("Image not found: " + path);
            return null;
        }
        return new Image(is);
    }

    /**
     * Returns the standard image filename for a given ship name.
     *
     * @param shipName The name of the ship.
     * @return Image file name corresponding to the ship.
     */
    private String getShipImageName(String shipName) {
        shipName = shipName.toLowerCase();
        if (shipName.contains("carrier")) return "carrier.png";
        if (shipName.contains("submarine")) return "submarine.png";
        if (shipName.contains("destroyer")) return "destroyer.png";
        return "plane.png";
    }

    /**
     * Retrieves a StackPane from a GridPane at the given coordinates.
     *
     * @param grid GridPane to search.
     * @param row  Row index.
     * @param col  Column index.
     * @return StackPane at the given coordinates, or null if not found.
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

    /** Rebuilds the player board view, including ships but not shot indicators. */
    public void rebuildPlayerBoard() {
        if (player == null) return;

        for (Node node : playerBoard.getChildren()) {
            if (node instanceof StackPane cell) {
                cell.getChildren().removeIf(n -> n instanceof ImageView);
            }
        }

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

    /** Rebuilds only the shot indicators (hits, misses, sinks) on the board. */
    public void rebuildPlayerShotsOnly() {
        if (player == null) return;

        int[][] cells = player.getBoard().getCells();

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {

                StackPane cell = getNodeFromGridPane(playerBoard, r, c);
                if (cell == null) continue;

                cell.getChildren().removeIf(n -> n instanceof ImageView);

                switch (cells[r][c]) {
                    case 2 -> addImageToCell(cell, "/edu/univalle/battleship/images/hit.png");
                    case 3 -> addImageToCell(cell, "/edu/univalle/battleship/images/sink.png");
                    case 4 -> addImageToCell(cell, "/edu/univalle/battleship/images/miss.png");
                }
            }
        }
    }
}
