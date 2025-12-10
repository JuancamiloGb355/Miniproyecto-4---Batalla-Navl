package edu.univalle.battleship.view;

import edu.univalle.battleship.controller.StartController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartStage extends Stage {

    private StartController controller;

    public StartStage() throws Exception {

        FXMLLoader loader = new FXMLLoader(
                StartStage.class.getResource("/edu/univalle/battleship/startView.fxml")
        );

        Parent root = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(root, 600, 400);
        setScene(scene);
        setTitle("Batalla Naval - Inicio");
        show();
    }

    public StartController getController() {
        return controller;
    }

    private static class Holder {
        private static StartStage INSTANCE;
    }

    public static StartStage getInstance() throws Exception {
        return Holder.INSTANCE != null
                ? Holder.INSTANCE
                : (Holder.INSTANCE = new StartStage());
    }
}
