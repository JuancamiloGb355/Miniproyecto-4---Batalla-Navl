package edu.univalle.battleship.controller;

import edu.univalle.battleship.model.*;
import edu.univalle.battleship.model.planeTextFiles.PlaneTextFileHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            // Inicializa el tablero para un juego nuevo
            PositionController pc = loader.getController();
            Player human = new Player();
            pc.setupForNewGame(human);

            GameManager.getInstance().setPositionController(pc);
            GameManager.getInstance().setPlayerBoardGrid(pc.getPlayerBoard());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button continueButton;

    @FXML
    private void handleContinue() {
        // Cargar jugadores desde archivo
        Player loadedPlayer = GameStateHandler.loadPlayer();
        MachinePlayer loadedMachine = GameStateHandler.loadMachine();

        if (loadedPlayer == null || loadedMachine == null) {
            System.out.println("No se encontró partida guardada.");
            return;
        }

        // Actualizar GameManager
        GameManager gm = GameManager.getInstance();
        gm.setHuman(loadedPlayer);
        gm.setMachine(loadedMachine);

        try {
            // Ventana del jugador
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/univalle/battleship/positionView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) playButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            PositionController pc = loader.getController();
            gm.setPositionController(pc);
            gm.setPlayerBoardGrid(pc.getPlayerBoard());
            pc.setPlayer(loadedPlayer);
            pc.setPlayer(loadedPlayer);

            // 1️⃣ primero barcos
            pc.rebuildPlayerBoard();

            // 2️⃣ luego hits / miss / sunk
            pc.rebuildPlayerShotsOnly();


            // Ventana del enemigo
            FXMLLoader enemyLoader = new FXMLLoader(getClass().getResource("/edu/univalle/battleship/enemyPreviewView.fxml"));
            Parent enemyRoot = enemyLoader.load();
            Stage enemyStage = new Stage();
            enemyStage.setScene(new Scene(enemyRoot));
            enemyStage.setTitle("Tablero Maquina");
            enemyStage.show();

            OpponentController enemyController = enemyLoader.getController();
            enemyController.setPlayers(loadedPlayer, loadedMachine); // Asegúrate de tener este método
            enemyController.rebuildOpponentBoard(); // Reconstruye barcos y disparos de la máquina

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Partida cargada correctamente.");
    }

}
