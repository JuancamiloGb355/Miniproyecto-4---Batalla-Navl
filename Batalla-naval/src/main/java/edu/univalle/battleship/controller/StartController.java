package edu.univalle.battleship.controller;

import edu.univalle.battleship.model.*;
import edu.univalle.battleship.model.planeTextFiles.PlaneTextFileHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class StartController {

    PlaneTextFileHandler planeTextFileHandler;




    @FXML
    private Button playButton;

    private StartModel model;

    public StartController() {
        model = new StartModel();
    }

    @FXML
    private void initialize() {
        System.out.println("StartController inicializado");
    }

    @FXML
    private void handlePlay(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/univalle/battleship/positionView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button continueButton;

    @FXML
    private void handleContinue() {
        // 1️⃣ Cargar jugadores desde archivo
        Player loadedPlayer = GameStateHandler.loadPlayer();
        MachinePlayer loadedMachine = GameStateHandler.loadMachine();

        if (loadedPlayer == null || loadedMachine == null) {
            System.out.println("No se encontró partida guardada.");
            return;
        }

        // 2️⃣ Limpiar tableros lógicos
        loadedPlayer.getBoard().clear();
        loadedMachine.getBoard().clear();

        // 3️⃣ Colocar barcos en los tableros lógicos
        for (Ship ship : loadedPlayer.getFleet()) {
            loadedPlayer.getBoard().placeShip(ship);
        }
        for (Ship ship : loadedMachine.getFleet()) {
            loadedMachine.getBoard().placeShip(ship);
        }

        // 4️⃣ Actualizar GameManager
        GameManager gm = GameManager.getInstance();
        gm.setHuman(loadedPlayer);
        gm.setMachine(loadedMachine);
        gm.setPlayerTurn(true);

        // 5️⃣ Abrir ventana del jugador
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/univalle/battleship/positionView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) playButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            // Obtener controlador del tablero del jugador
            PositionController positionController = loader.getController();
            gm.setPositionController(positionController);
            gm.setPlayerBoardGrid(positionController.getPlayerBoard());

            // Reconstruir tablero del jugador con barcos y disparos
            positionController.setPlayer(loadedPlayer);
            positionController.rebuildPlayerBoard();

            // Reconstruir disparos previos del enemigo
            boolean[][] hits = loadedPlayer.getBoard().getHits();
            boolean[][] misses = loadedPlayer.getBoard().getMisses();

            for (int r = 0; r < Board.SIZE; r++) {
                for (int c = 0; c < Board.SIZE; c++) {
                    if (hits[r][c]) {
                        positionController.updateCellAsHit(r, c);
                    } else if (misses[r][c]) {
                        positionController.updateCellAsMiss(r, c);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 6️⃣ Abrir ventana del enemigo
        try {
            FXMLLoader loaderEnemy = new FXMLLoader(getClass().getResource("/edu/univalle/battleship/enemyPreviewView.fxml"));
            Parent rootEnemy = loaderEnemy.load();

            Stage enemyStage = new Stage();
            enemyStage.setTitle("Opponent Board");
            enemyStage.setScene(new Scene(rootEnemy));
            enemyStage.show();

            OpponentController enemyController = loaderEnemy.getController();
            enemyController.setPlayers(loadedPlayer, loadedMachine);
            enemyController.rebuildOpponentBoard();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Partida cargada correctamente.");
    }

}
