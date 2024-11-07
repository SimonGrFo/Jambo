package com.example.jambo;

import com.example.jambo.controllers.JamboController;
import com.example.jambo.ui.JamboUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Jambo extends Application {
    @Override
    public void start(Stage primaryStage) {
        JamboUI ui = new JamboUI();
        JamboController controller = new JamboController(ui);
        controller.initializeStage(primaryStage);
        Scene scene = ui.createScene(controller);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}