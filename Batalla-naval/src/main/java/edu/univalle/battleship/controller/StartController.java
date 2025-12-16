package edu.univalle.battleship.controller;

import edu.univalle.battleship.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

/**
 * Controller for the start menu of the Battleship game.
 * <p>
 * This controller handles:
 * <ul>
 *     <li>Starting a new game</li>
 *     <li>Loading a previously saved game</li>
 *     <li>Transitioning between scenes</li>
 * </ul>
 */
public class StartController {

    /**
     * Button used to start a new game or load an existing one.
     */
    @FXML
    private Button playButton;

    /**
     * Initializes the controller.
     * This method is automatically called by JavaFX after the FXML is loaded.
     */
    @FXML
    private void initialize() {
        System.out.println("StartController inicializado");
    }

    /**
     * Handles the "Play" button action.
     * <p>
     * Loads the ship positioning view, initializes a new human player,
     * and prepares the {@link GameManager} for a new game.
     *
     * @param event the action event triggered by clicking the Play button
     */
    @FXML
    private void handlePlay(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/univalle/battleship/positionView.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            // Initialize a new game
            PositionController pc = loader.getController();
            Player human = new Player();
            pc.setupForNewGame(human);

            GameManager.getInstance().setPositionController(pc);
            GameManager.getInstance().setPlayerBoardGrid(pc.getPlayerBoard());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the "Continue" button action.
     * <p>
     * Loads a previously saved game state, restores both the human
     * and machine players, rebuilds the boards, and opens the
     * opponent preview window.
     */
    @FXML
    private void handleContinue() {

        // Load saved players
        Player loadedPlayer = GameStateHandler.loadPlayer();
        MachinePlayer loadedMachine = GameStateHandler.loadMachine();

        if (loadedPlayer == null || loadedMachine == null) {
            System.out.println("No se encontró partida guardada.");
            return;
        }

        // Update GameManager with loaded data
        GameManager gm = GameManager.getInstance();
        gm.setHuman(loadedPlayer);
        gm.setMachine(loadedMachine);

        try {
            // Player board window
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/univalle/battleship/positionView.fxml")
            );
            Parent root = loader.load();
            Stage stage = (Stage) playButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            PositionController pc = loader.getController();
            gm.setPositionController(pc);
            gm.setPlayerBoardGrid(pc.getPlayerBoard());
            pc.setPlayer(loadedPlayer);
            pc.setPlayer(loadedPlayer);

            // Rebuild ships first
            pc.rebuildPlayerBoard();

            // Then rebuild hits, misses, and sunk ships
            pc.rebuildPlayerShotsOnly();

            // Enemy board window
            FXMLLoader enemyLoader = new FXMLLoader(
                    getClass().getResource("/edu/univalle/battleship/enemyPreviewView.fxml")
            );
            Parent enemyRoot = enemyLoader.load();
            Stage enemyStage = new Stage();
            enemyStage.setScene(new Scene(enemyRoot));
            enemyStage.setTitle("Tablero Maquina");
            enemyStage.show();

            OpponentController enemyController = enemyLoader.getController();
            enemyController.setPlayers(loadedPlayer, loadedMachine);
            enemyController.rebuildOpponentBoard();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Partida cargada correctamente.");
    }

}
