package edu.univalle.battleship;

import edu.univalle.battleship.view.StartStage;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point of the Battleship application.
 * <p>
 * This class extends {@link javafx.application.Application} and
 * initializes the primary stage by displaying the start stage of the game.
 */
public class Main extends Application {

    /**
     * Initializes and shows the primary stage of the application.
     *
     * @param primaryStage the primary stage provided by the JavaFX runtime
     * @throws Exception if an error occurs during stage initialization
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        StartStage.getInstance();
    }

}
