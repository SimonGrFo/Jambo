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

//TODO - change the way songs are displayed into more of a table format

//TODO - when restarting songs get reformeted=! uh oh!

//TODO - implement .flac file support?? maybe? would probably need to rework a lot
//TODO - fix no duplicate songs between playlists
//TODO - finish settings menu
//TODO - css styling
