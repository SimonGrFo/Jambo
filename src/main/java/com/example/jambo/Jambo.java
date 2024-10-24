package com.example.jambo;

import com.example.jambo.controllers.JamboController;
import com.example.jambo.ui.JamboUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class Jambo extends Application {
    @Override
    public void start(Stage primaryStage) {
        JamboUI ui = new JamboUI();
        JamboController controller = new JamboController(ui);
        controller.initializeStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
