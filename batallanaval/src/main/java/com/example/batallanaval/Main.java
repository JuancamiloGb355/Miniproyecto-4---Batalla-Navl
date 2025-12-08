package com.example.batallanaval;

import javafx.application.Application;
import javafx.stage.Stage;
import com.example.batallanaval.controller.StartController;
import com.example.batallanaval.view.StartStage;
import java.io.IOException;

public class Main extends Application {

    /**
     * Called automatically by the JavaFX runtime when the application starts.
     * <p>
     * This method does not configure the provided primary stage; it simply
     * initializes the first UI stage through {@code StartStage}, which handles
     * loading the FXML and showing the window.
     * </p>
     *
     * @param primaryStage the main stage supplied by the JavaFX runtime
     * @throws IOException if an error occurs while loading the required UI resources
     */

    @Override
    public void start(Stage primaryStage) throws IOException {
        StartStage.getInstance();

    }
}

