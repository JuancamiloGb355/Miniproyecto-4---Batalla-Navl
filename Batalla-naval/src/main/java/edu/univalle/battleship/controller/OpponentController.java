package edu.univalle.battleship.controller;

import edu.univalle.battleship.model.*;
import edu.univalle.battleship.model.planeTextFiles.PlaneTextFileHandler;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller class for handling the opponent's board in the Battleship game.
 * Manages the opponent grid, handles player's shots, reveals ships,
 * and controls the machine player's turn logic.
 */
public class OpponentController {

    /** GridPane representing the opponent's board. */
    @FXML
    private GridPane opponentBoard;

    /** Root layout for the scene. */
    @FXML
    BorderPane root;

    /** Button to save the game and exit. */
    @FXML
    private Button btnSaveExit;

    /** AI-controlled opponent player. */
    private MachinePlayer machine;

    /** Human player. */
    private Player human;

    /** Counter for the number of opponent ships sunk by the player. */
    private int numberOfSunkenShips = 0;

    /** Handler for reading/writing plain text files. */
    private PlaneTextFileHandler planeTextFileHandler;

    /**
     * Initializes the opponent board controller.
     * Sets up the board, assigns players, and configures key events.
     */
    @FXML
    public void initialize() {
        GameManager gm = GameManager.getInstance();
        this.human = gm.getHuman();
        this.machine = gm.getMachine();

        planeTextFileHandler = new PlaneTextFileHandler();

        createBoard();

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

    /**
     * Reveals all opponent ships on the board with their respective images.
     */
    private void revealShips() {
        for (Ship ship : machine.getFleet()) {
            String imgName =
                    ship.getName().toLowerCase().contains("carrier") ? "carrier.png" :
                            ship.getName().toLowerCase().contains("submarine") ? "submarine.png" :
                                    ship.getName().toLowerCase().contains("destroyer") ? "destroyer.png" :
                                            "plane.png";

            int[][] positions = ship.getPositions();
            int startRow = positions[0][0];
            int startCol = positions[0][1];
            boolean horizontal = positions.length > 1 && positions[0][0] == positions[1][0];
            int shipSize = positions.length;

            Image img = new Image(getClass().getResourceAsStream("/edu/univalle/battleship/images/" + imgName));
            ImageView shipView = new ImageView(img);
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

            GridPane.setRowIndex(shipView, startRow);
            GridPane.setColumnIndex(shipView, startCol);
            if (horizontal) GridPane.setColumnSpan(shipView, shipSize);
            else GridPane.setRowSpan(shipView, shipSize);

            opponentBoard.getChildren().add(shipView);
        }
    }

    /**
     * Creates an empty opponent board and configures click events for each cell.
     */
    private void createBoard() {
        int size = Board.SIZE;

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

    /**
     * Handles a shot fired by the player at a given cell.
     *
     * @param row  The row index of the cell.
     * @param col  The column index of the cell.
     * @param cell The StackPane representing the target cell.
     */
    private void handleShot(int row, int col, StackPane cell) {
        GameManager gm = GameManager.getInstance();

        Board board = machine.getBoard();
        int[][] cells = board.getCells();

        if (cells[row][col] >= 2) return; // Already shot

        String result = board.receiveShot(row, col); // hit/miss/sunk

        switch (result) {
            case "hit":
                cells[row][col] = 2;
                addImageToCell(cell, "/edu/univalle/battleship/images/hit.png");
                break;
            case "miss":
                cells[row][col] = 4;
                addImageToCell(cell, "/edu/univalle/battleship/images/miss.png");
                gm.setPlayerTurn(false);
                machineTurnWithDelay();
                break;
            default:
                if (result.startsWith("sunk:")) {
                    numberOfSunkenShips++;
                    String sunkName = result.split(":")[1].trim();

                    Ship sunkShip = machine.getFleet().stream()
                            .filter(s -> s.getName().trim().equals(sunkName))
                            .findFirst().orElse(null);

                    if (sunkShip != null) {
                        for (int[] pos : sunkShip.getPositions()) {
                            StackPane shipCell = getNodeFromGridPane(opponentBoard, pos[0], pos[1]);
                            if (shipCell != null) {
                                cells[pos[0]][pos[1]] = 3;
                                shipCell.getChildren().clear();
                                addImageToCell(shipCell, "/edu/univalle/battleship/images/sink.png");
                            }
                        }
                    }

                    if (GameManager.getInstance().isMachineDefeated()) {
                        closeWindow();
                        endGame("¡HAS GANADO!");
                        return;
                    }
                }
                break;
        }
    }

    /**
     * Starts the machine player's turn after a short delay.
     */
    private void machineTurnWithDelay() {
        PauseTransition delay = new PauseTransition(Duration.seconds(0.7));
        delay.setOnFinished(e -> machineTurnLogic());
        delay.play();
    }

    /**
     * Executes the machine player's shooting logic and updates the player's board.
     */
    private void machineTurnLogic() {
        MachinePlayer machine = GameManager.getInstance().getMachine();
        Player human = GameManager.getInstance().getHuman();
        GridPane playerBoard = GameManager.getInstance().getPlayerBoardGrid();
        int[][] cells = human.getBoard().getCells();

        String result = machine.shoot(human);
        int[] last = machine.getLastShotCoordinates();
        int row = last[0];
        int col = last[1];

        StackPane targetCell = getNodeFromGridPane(playerBoard, row, col);

        if (result.equals("miss")) {
            cells[row][col] = 4;
        } else if (result.equals("hit")) {
            cells[row][col] = 2;
        } else if (result.startsWith("sunk:")) {
            String sunkName = result.split(":")[1].trim();
            Ship sunkShip = human.getFleet().stream()
                    .filter(s -> s.getName().trim().equals(sunkName))
                    .findFirst().orElse(null);

            if (sunkShip != null) {
                for (int[] pos : sunkShip.getPositions()) {
                    StackPane cellToSink = getNodeFromGridPane(playerBoard, pos[0], pos[1]);
                    if (cellToSink != null) {
                        cells[pos[0]][pos[1]] = 3;
                        cellToSink.getChildren().removeIf(n -> n instanceof ImageView);
                        addImageToCell(cellToSink, "/edu/univalle/battleship/images/sink.png");
                    }
                }
            }
        }

        if (targetCell != null) {
            targetCell.getChildren().removeIf(n -> n instanceof ImageView);

            if (cells[row][col] == 2)
                addImageToCell(targetCell, "/edu/univalle/battleship/images/hit.png");
            else if (cells[row][col] == 3)
                addImageToCell(targetCell, "/edu/univalle/battleship/images/sink.png");
            else if (cells[row][col] == 4)
                addImageToCell(targetCell, "/edu/univalle/battleship/images/miss.png");
        }

        if (GameManager.getInstance().isHumanDefeated()) {
            closeWindow();
            endGame("¡HAS PERDIDO!");
            return;
        }

        if (result.equals("miss")) {
            GameManager.getInstance().setPlayerTurn(true);
        } else {
            machineTurnWithDelay();
        }
    }

    /**
     * Retrieves the StackPane node at a specific row and column from a GridPane.
     *
     * @param grid The GridPane to search.
     * @param row  The target row.
     * @param col  The target column.
     * @return The StackPane at the specified coordinates, or null if not found.
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
     * Adds an image to a cell.
     *
     * @param cell The target StackPane cell.
     * @param path The resource path of the image.
     */
    private void addImageToCell(StackPane cell, String path) {
        ImageView img = new ImageView(new Image(getClass().getResourceAsStream(path)));
        img.setFitWidth(40);
        img.setFitHeight(40);
        cell.getChildren().add(img);
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    /**
     * Displays the game end alert and resets the game state.
     *
     * @param message The message to show in the alert.
     */
    private void endGame(String message) {
        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        GameManager.getInstance().resetGame();
    }

    /**
     * Handles the save and exit button action.
     * Saves the current game state before closing the window.
     */
    @FXML
    private void handleSaveExit() {
        if (human != null && machine != null) GameStateHandler.saveGame(human, machine);
        Stage stage = (Stage) btnSaveExit.getScene().getWindow();
        stage.close();
    }

    /**
     * Rebuilds the opponent board, updating the UI to reflect hits, misses, and sunk ships.
     */
    public void rebuildOpponentBoard() {
        opponentBoard.getChildren().clear();
        int size = Board.SIZE;
        int[][] boardArray = machine.getBoard().getCells();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(40, 40);
                cell.setStyle("-fx-border-color: white; -fx-background-color: #87CEFA;");
                final int r = row;
                final int c = col;
                cell.setOnMouseClicked(event -> handleShot(r, c, cell));
                opponentBoard.add(cell, col, row);

                switch (boardArray[row][col]) {
                    case 2 -> addImageToCell(cell, "/edu/univalle/battleship/images/hit.png");
                    case 3 -> addImageToCell(cell, "/edu/univalle/battleship/images/sink.png");
                    case 4 -> addImageToCell(cell, "/edu/univalle/battleship/images/miss.png");
                }
            }
        }
    }

    /**
     * Sets the human and machine players for this controller.
     *
     * @param human   The human player.
     * @param machine The machine player.
     */
    public void setPlayers(Player human, MachinePlayer machine) {
        this.human = human;
        this.machine = machine;
    }
}
