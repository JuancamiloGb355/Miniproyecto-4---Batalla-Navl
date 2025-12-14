package edu.univalle.battleship;

import edu.univalle.battleship.view.StartStage;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        StartStage.getInstance();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
