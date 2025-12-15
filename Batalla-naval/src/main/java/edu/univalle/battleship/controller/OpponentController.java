package edu.univalle.battleship.controller;

import edu.univalle.battleship.model.*;
import edu.univalle.battleship.model.planeTextFiles.PlaneTextFileHandler;
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

import java.io.IOException;

public class OpponentController {

    @FXML
    private GridPane opponentBoard;

    @FXML
    BorderPane root;

    // Referencia al botón en el FXML
    @FXML
    private Button btnSaveExit;

    private MachinePlayer machine;
    private Player human;

    private int numberofsunkenships = 0;

    private PlaneTextFileHandler planeTextFileHandler;

    @FXML
    public void initialize() {

        // ⬅️ Obtener jugadores desde GameManager
        GameManager gm = GameManager.getInstance();
        this.human = gm.getHuman();
        this.machine = gm.getMachine();

        planeTextFileHandler = new PlaneTextFileHandler();

        createBoard();

        // Tecla para mostrar barcos (debug)
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

    // ---------------------------
    // DIBUJAR BARCOS (debug)
    // ---------------------------
    private void revealShips() {
        for (Ship ship : machine.getFleet()) {
            // Elegir la imagen según el nombre real del barco
            String imgName =
                    ship.getName().toLowerCase().contains("carrier") ? "carrier.png" :
                            ship.getName().toLowerCase().contains("submarine") ? "submarine.png" :
                                    ship.getName().toLowerCase().contains("destroyer") ? "destroyer.png" :
                                            "plane.png";

            // Tomar la primera posición del barco para ubicar la imagen
            int[][] positions = ship.getPositions();
            int startRow = positions[0][0];
            int startCol = positions[0][1];
            boolean horizontal = positions.length > 1 && positions[0][0] == positions[1][0];
            int shipSize = positions.length;

            Image img = new Image(getClass().getResourceAsStream(
                    "/edu/univalle/battleship/images/" + imgName));
            ImageView shipView = new ImageView(img);

            shipView.setMouseTransparent(true);

            // Ajustar tamaño y rotación según orientación
            if (horizontal) {
                shipView.setRotate(-90);
                shipView.setFitWidth(40);
                shipView.setFitHeight(40 * shipSize);
                double offset = (shipSize - 1) * 20.0;
                shipView.setTranslateX(offset);
                shipView.setTranslateY(0);
            } else {
                shipView.setFitWidth(40);
                shipView.setFitHeight(40 * shipSize);
            }

            // Colocar en GridPane
            GridPane.setRowIndex(shipView, startRow);
            GridPane.setColumnIndex(shipView, startCol);
            if (horizontal) {
                GridPane.setColumnSpan(shipView, shipSize);
            } else {
                GridPane.setRowSpan(shipView, shipSize);
            }

            opponentBoard.getChildren().add(shipView);
        }
    }



    // ---------------------------
    // CREAR TABLERO
    // ---------------------------
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

    // ---------------------------
    // DISPARO DEL JUGADOR
    // ---------------------------
    private void handleShot(int row, int col, StackPane cell) {

        GameManager gm = GameManager.getInstance();

        if (!gm.isPlayerTurn()) {
            System.out.println("Not your turn!");
            return;
        }

        Board board = machine.getBoard();

        if (board.isShotRepeated(row, col)) {
            System.out.println("Already shot here!");
            return;
        }

        String result = board.receiveShot(row, col);

        switch (result) {

            case "hit":
                addImageToCell(cell, "/edu/univalle/battleship/images/hit.png");
                System.out.println("Hit! You can shoot again.");
                break;

            case "miss":
                addImageToCell(cell, "/edu/univalle/battleship/images/miss.png");
                System.out.println("Miss! Machine's turn.");
                gm.setPlayerTurn(false);
                machineTurn();
                break;

            default: // sunk:Nombre
                if (result.startsWith("sunk:")) {

                    numberofsunkenships++;
                    if (numberofsunkenships >= 10){
                        System.out.println("¡Has ganado el juego!");
                        Stage stage = (Stage) root.getScene().getWindow();
                        stage.close();

                    }
                    try {
                        planeTextFileHandler.write("player_data.csv", Integer.toString(numberofsunkenships));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String sunkShipName = result.split(":")[1];

                    Ship sunkShip = null;
                    for (Ship s : machine.getFleet()) {
                        if (s.getName().trim().equals(sunkShipName.trim())) {
                            sunkShip = s;
                            break;
                        }
                    }

                    if (sunkShip != null) {

                        for (int[] pos : sunkShip.getPositions()) {
                            StackPane shipCell = getNodeFromGridPane(opponentBoard, pos[0], pos[1]);
                            if (shipCell != null) {
                                shipCell.getChildren().clear();
                                addImageToCell(shipCell, "/edu/univalle/battleship/images/sink.png");
                            }
                        }
                    }

                    System.out.println("Sunk " + sunkShipName + "! You can shoot again.");

                    if (GameManager.getInstance().isMachineDefeated()) {
                        PositionController pc =
                                GameManager.getInstance().getPositionController();

                        if (pc != null) {
                            Platform.runLater(() -> {
                                pc.clearShotsOnly();
                                pc.btnStartGame.setDisable(false);
                                pc.btnStartGame.setText("Try again");
                            });
                        }

                        closeWindow();
                        endGame("GANASTE 🎉");
                        return;
                    }
                }
                break;
        }
    }


    private void machineTurn() {

        MachinePlayer machine = GameManager.getInstance().getMachine();
        Player human = GameManager.getInstance().getHuman();

        String result = machine.shoot(human);

        int[] last = machine.getLastShotCoordinates();
        int row = last[0];
        int col = last[1];

        GridPane playerBoardGrid = GameManager.getInstance().getPlayerBoardGrid();
        StackPane targetCell = getNodeFromGridPane(playerBoardGrid, row, col);

        if (result.equals("miss")) {
            addImageToCell(targetCell, "/edu/univalle/battleship/images/miss.png");
        } else if (result.equals("hit")) {
            addImageToCell(targetCell, "/edu/univalle/battleship/images/hit.png");
        } else if (result.startsWith("sunk:")) {

            String sunkShipName = result.split(":")[1].trim();

            // Buscar ese barco en la flota del jugador
            Ship sunkShip = null;
            for (Ship s : human.getFleet()) {
                if (s.getName().trim().equals(sunkShipName)) {
                    sunkShip = s;
                    break;
                }
            }

            if (sunkShip != null) {

                for (int[] pos : sunkShip.getPositions()) {
                    StackPane cellToSink = getNodeFromGridPane(playerBoardGrid, pos[0], pos[1]);

                    if (cellToSink != null) {
                        cellToSink.getChildren().clear();
                        addImageToCell(cellToSink, "/edu/univalle/battleship/images/sink.png");
                    }
                }
            }
        }

        if (GameManager.getInstance().isHumanDefeated()) {
            PositionController pc =
                    GameManager.getInstance().getPositionController();

            if (pc != null) {
                Platform.runLater(() -> {
                    pc.clearShotsOnly();
                    pc.btnStartGame.setDisable(false);
                    pc.btnStartGame.setText("Try again");
                });
            }

            closeWindow();
            endGame("PERDISTE 💀");
            return;
        }

        System.out.println("Machine shot result: " + result);

        if (result.equals("miss")) {
            System.out.println("Machine missed! Player's turn again.");
            GameManager.getInstance().setPlayerTurn(true);
        } else {
            System.out.println("Machine hit or sunk! Machine shoots again.");
            machineTurn();
        }
    }

    // ---------------------------
    // UTILS
    // ---------------------------

    private StackPane getNodeFromGridPane(GridPane grid, int row, int col) {
        for (var node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) == row &&
                    GridPane.getColumnIndex(node) == col) {
                return (StackPane) node;
            }
        }
        return null;
    }

    private void addImageToCell(StackPane cell, String imgPath) {
        ImageView img = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
        img.setFitWidth(40);
        img.setFitHeight(40);
        cell.getChildren().add(img);
    }

    public int getNumberofsunkenships() {
        return numberofsunkenships;
    }

    public void setNumberofsunkenships(int numberofsunkenships) {
        this.numberofsunkenships = numberofsunkenships;
    }

    private void disableBoard() {
        opponentBoard.setDisable(true);
    }

    private void showEndMessage(String msg) {
        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void closeWindow() {
        javafx.stage.Stage stage = (javafx.stage.Stage) opponentBoard.getScene().getWindow();
        stage.close();
    }

    private void endGame(String message) {

        // Mostrar mensaje
        showEndMessage(message);

        // Cerrar ventana del oponente
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();

        // Reset GameManager
        GameManager.getInstance().resetGame();

        // Obtener PositionController y resetear tablero
        PositionController pc =
                (PositionController) GameManager.getInstance()
                        .getPlayerBoardGrid()
                        .getScene()
                        .getUserData();

    }

    @FXML
    private void handleSaveExit() {
        if (human != null && machine != null) {
            GameStateHandler.saveGame(human, machine); // guardar partida
        }

        // Cerrar la ventana
        Stage stage = (Stage) btnSaveExit.getScene().getWindow();
        stage.close();
    }

    // Permite pasar los jugadores al controlador desde otra clase
    public void setPlayers(Player human, MachinePlayer machine) {
        this.human = human;
        this.machine = machine;
    }

    public void rebuildOpponentBoard() {
        opponentBoard.getChildren().clear(); // limpiar
        int size = Board.SIZE;

        // Crear celdas vacías
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

        // Colocar disparos hechos
        boolean[][] hits = machine.getBoard().getHits();
        boolean[][] misses = machine.getBoard().getMisses();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                StackPane cell = getNodeFromGridPane(opponentBoard, row, col);
                if (cell != null) {
                    if (hits[row][col]) addImageToCell(cell, "/edu/univalle/battleship/images/hit.png");
                    if (misses[row][col]) addImageToCell(cell, "/edu/univalle/battleship/images/miss.png");
                }
            }
        }
    }
}