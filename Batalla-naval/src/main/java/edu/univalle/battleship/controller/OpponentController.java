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

import java.io.IOException;

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

                    if (numberOfSunkenShips >= 10) {
                        closeWindow();
                        endGame("¡HAS GANADO!");
                        return;
                    }
                }
                break;
        }
    }

    private void machineTurnWithDelay() {
        PauseTransition delay = new PauseTransition(Duration.seconds(0.7));
        delay.setOnFinished(e -> machineTurnLogic());
        delay.play();
    }

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

        // Pintar disparo actual
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

        // 👇 AQUÍ ESTÁ LA CLAVE
        if (result.equals("miss")) {
            GameManager.getInstance().setPlayerTurn(true);
        } else {
            machineTurnWithDelay(); // ⏳ siguiente disparo TAMBIÉN con delay
        }
    }




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

    private void addImageToCell(StackPane cell, String path) {
        ImageView img = new ImageView(new Image(getClass().getResourceAsStream(path)));
        img.setFitWidth(40);
        img.setFitHeight(40);
        cell.getChildren().add(img);
    }

    private void closeWindow() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    private void endGame(String message) {
        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        GameManager.getInstance().resetGame();
    }

    @FXML
    private void handleSaveExit() {
        if (human != null && machine != null) GameStateHandler.saveGame(human, machine);
        Stage stage = (Stage) btnSaveExit.getScene().getWindow();
        stage.close();
    }

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

    public void setPlayers(Player human, MachinePlayer machine) {
        this.human = human;
        this.machine = machine;
    }

}
