package edu.univalle.battleship.controller;

import edu.univalle.battleship.model.StartModel;
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
    void handleContinue(ActionEvent event) throws IOException {
        planeTextFileHandler = new PlaneTextFileHandler();

        String [] data = planeTextFileHandler.read("player_data.csv");
        int numberofSsunkenships = Integer.parseInt(data[0]);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/edu/univalle/battleship/enemyPreviewView.fxml"));
        Parent root = loader.load();
        OpponentController controller = loader.getController();
        controller.setNumberofsunkenships(numberofSsunkenships);
        Stage stage = new Stage();
        stage.setTitle("Opponent Board");
        stage.setScene(new Scene(root));
        stage.show();

        System.out.println(controller.getNumberofsunkenships());




    }
}
