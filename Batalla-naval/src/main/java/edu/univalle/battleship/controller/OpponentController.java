package edu.univalle.battleship.controller;

import edu.univalle.battleship.model.*;
import edu.univalle.battleship.model.planeTextFiles.PlaneTextFileHandler;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
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
 * Controller for the opponent board.
 * <p>
 * Handles the interaction between the human player and the machine player.
 * Manages shooting, showing hits/misses/sunk ships, machine AI moves, and saving/exiting.
 */
public class OpponentController {

    @FXML
    private GridPane opponentBoard;

    @FXML
    BorderPane root;

    @FXML
    private Button btnSaveExit;

    private MachinePlayer machine;
    private Player human;

    private int numberOfSunkenShips = 0;

    private PlaneTextFileHandler planeTextFileHandler;

    /**
     * JavaFX initialization method.
     * Initializes the human and machine players, sets up the opponent board, and listens for key events.
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
     * Reveals all machine ships on the board, for debugging or cheat purposes.
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
     * Creates the opponent board UI, with 40x40 cells and click handlers for shots.
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
     * Handles a shot by the human player on a given cell.
     * Updates the board array, adds images, and triggers machine turn if necessary.
     *
     * @param row  row index of the shot
     * @param col  column index of the shot
     * @param cell the StackPane cell clicked
     */
    private void handleShot(int row, int col, StackPane cell) {
        GameManager gm = GameManager.getInstance();

        if (!gm.isPlayerTurn()) return;

        Board board = machine.getBoard();
        int[][] cells = board.getCells();

        if (cells[row][col] >= 2) return; // ya fue disparado

        String result = board.receiveShot(row, col); // recibe hit/miss/sunk y actualiza array

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
     * Executes the machine's turn after a small delay.
     */
    private void machineTurnWithDelay() {
        PauseTransition delay = new PauseTransition(Duration.seconds(0.7));
        delay.setOnFinished(e -> machineTurnLogic());
        delay.play();
    }

    /**
     * Handles the machine's shooting logic.
     * Updates the human player's board, images, and ship hit status.
     * Checks for victory/defeat conditions.
     */
    private void machineTurnLogic() {
        MachinePlayer machine = GameManager.getInstance().getMachine();
        Player human = GameManager.getInstance().getHuman();
        GridPane playerBoard = GameManager.getInstance().getPlayerBoardGrid();
        int[][] cells = human.getBoard().getCells();

        // La máquina hace su disparo usando la estrategia
        String result = machine.shoot(human); // Este método maneja la lógica de disparo
        int[] last = machine.getLastShotCoordinates(); // Coordenadas del último disparo
        int row = last[0];
        int col = last[1];

        StackPane targetCell = getNodeFromGridPane(playerBoard, row, col);

        // Maneja el resultado del disparo
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

        // Pintar disparo actual en la interfaz
        if (targetCell != null) {
            targetCell.getChildren().removeIf(n -> n instanceof ImageView);

            if (cells[row][col] == 2)
                addImageToCell(targetCell, "/edu/univalle/battleship/images/hit.png");
            else if (cells[row][col] == 3)
                addImageToCell(targetCell, "/edu/univalle/battleship/images/sink.png");
            else if (cells[row][col] == 4)
                addImageToCell(targetCell, "/edu/univalle/battleship/images/miss.png");
        }

        // Actualizar hits[] para cada barco humano
        for (Ship ship : human.getFleet()) {
            int[][] positions = ship.getPositions();
            for (int i = 0; i < positions.length; i++) {
                int r = positions[i][0];
                int c = positions[i][1];
                if (cells[r][c] == 2 || cells[r][c] == 3) {
                    ship.hitAt(r, c);
                }
            }
        }

        // Verificar si alguien ha sido derrotado
        if (GameManager.getInstance().isHumanDefeated()) {
            closeWindow();
            endGame("¡HAS PERDIDO!");
            return;
        }

        if (GameManager.getInstance().isMachineDefeated()) {
            closeWindow();
            endGame("¡HAS GANADO!");
            return;
        }

        // Si el disparo fue un "miss", se le da el turno al jugador
        if (result.equals("miss")) {
            GameManager.getInstance().setPlayerTurn(true);
        } else {
            machineTurnWithDelay(); // Si fue un "hit" o "sunk", sigue el turno de la máquina con delay
        }
    }

    /**
     * Finds a StackPane cell in a GridPane at the specified row and column.
     *
     * @param grid the GridPane
     * @param row  the row index
     * @param col  the column index
     * @return the StackPane cell or null if not found
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
     * Adds an image to a specific cell.
     *
     * @param cell the target cell
     * @param path path of the image resource
     */
    private void addImageToCell(StackPane cell, String path) {
        ImageView img = new ImageView(new Image(getClass().getResourceAsStream(path)));
        img.setFitWidth(40);
        img.setFitHeight(40);
        cell.getChildren().add(img);
    }

    /**
     * Closes the opponent board window.
     */
    private void closeWindow() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows a game-end message and resets the game state.
     *
     * @param message the message to show
     */
    private void endGame(String message) {
        Platform.runLater(() -> {
            javafx.scene.control.Alert alert =
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();

            GameManager.getInstance().resetGame();
        });
    }

    /**
     * Handles saving the game state and exiting the opponent board.
     */
    @FXML
    private void handleSaveExit() {
        if (human != null && machine != null) GameStateHandler.saveGame(human, machine);
        Stage stage = (Stage) btnSaveExit.getScene().getWindow();
        stage.close();
    }

    /**
     * Rebuilds the opponent board from the machine's current board state.
     * Displays hits, misses, and sunk ships.
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
     * @param human   the human player
     * @param machine the machine player
     */
    public void setPlayers(Player human, MachinePlayer machine) {
        this.human = human;
        this.machine = machine;
    }
}
