package com.example.batallanaval.view;

import com.example.batallanaval.controller.StartController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartStage extends Stage {
    StartController startController;

    public StartStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartStage.class.getResource("/com/example/batallanaval/startView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        setTitle("Hola");
        setScene(scene);
        show();
    }

    public StartController getStartController() {
        return startController;
    }

    public static StartStage getInstance() throws IOException {
        return StartStage.StartStageHolder.INSTANCE != null ?
                StartStageHolder.INSTANCE : (StartStageHolder.INSTANCE = new StartStage());
    }

    public static void deleteInstance() {
        StartStageHolder.INSTANCE.close();
        StartStageHolder.INSTANCE = null;
    }

    private static class StartStageHolder {
        private static StartStage INSTANCE;
    }
}

