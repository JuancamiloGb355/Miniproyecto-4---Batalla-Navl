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
 * Controller for the start screen of the Battleship game.
 * <p>
 * Handles the "Play" button for a new game and the "Continue" button to load a saved game.
 */
public class StartController {

    @FXML
    private Button playButton;

    private StartModel model;

    /**
     * Default constructor. Initializes the model.
     */
    public StartController() {
        model = new StartModel();
    }

    /**
     * Called automatically by JavaFX after the FXML elements are loaded.
     */
    @FXML
    private void initialize() {
        System.out.println("StartController initialized");
    }

    /**
     * Handles the "Play" button click to start a new game.
     *
     * @param event the action event triggered by clicking the play button
     */
    @FXML
    private void handlePlay(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/univalle/battleship/positionView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            // Initialize the board for a new game
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
     * Handles the "Continue" button click to load a saved game.
     * <p>
     * Loads the human player and machine player from a save file, updates the GameManager,
     * rebuilds the player and opponent boards, and restores hits, misses, and sunk ships.
     */
    @FXML
    private void handleContinue() {
        // Load saved players
        Player loadedPlayer = GameStateHandler.loadPlayer();
        MachinePlayer loadedMachine = GameStateHandler.loadMachine();

        if (loadedPlayer == null || loadedMachine == null) {
            System.out.println("No saved game found.");
            return;
        }

        GameManager gm = GameManager.getInstance();
        gm.setHuman(loadedPlayer);
        gm.setMachine(loadedMachine);

        try {
            // Player window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/univalle/battleship/positionView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) playButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            PositionController pc = loader.getController();
            gm.setPositionController(pc);
            gm.setPlayerBoardGrid(pc.getPlayerBoard());
            pc.setPlayer(loadedPlayer);

            // Rebuild player board and shots
            pc.rebuildPlayerBoard();
            pc.rebuildPlayerShotsOnly();

            // Opponent window
            FXMLLoader enemyLoader = new FXMLLoader(getClass().getResource("/edu/univalle/battleship/enemyPreviewView.fxml"));
            Parent enemyRoot = enemyLoader.load();
            Stage enemyStage = new Stage();
            enemyStage.setScene(new Scene(enemyRoot));
            enemyStage.setTitle("Machine Board");
            enemyStage.show();

            OpponentController enemyController = enemyLoader.getController();
            enemyController.setPlayers(loadedPlayer, loadedMachine);
            enemyController.rebuildOpponentBoard();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saved game loaded successfully.");
    }

}
