package edu.univalle.battleship;

import edu.univalle.battleship.view.StartStage;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point of the Battleship application.
 * <p>
 * This class launches the JavaFX application and initializes
 * the primary stage through {@link StartStage}.
 * </p>
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application.
     * <p>
     * This method is automatically called by the JavaFX runtime.
     * It initializes the main window of the application.
     * </p>
     *
     * @param primaryStage the primary stage provided by the JavaFX runtime
     * @throws Exception if an error occurs during application startup
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        StartStage.getInstance();
    }
}
