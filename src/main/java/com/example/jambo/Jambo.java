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

// TODO - Playlist
//        Save/load/delete multiple playlists
//        Playlist naming and organization
//        (saved into json like currently?)
// TODO - Settings
//        will have features such as Recently played tracks, Most played tracks,
//        Audio visualizer, crossfade, Keyboard shortcuts,
// TODO - Search functionality
// TODO - Lyrics display (from embedded metadata or online sources)?? would be hard to implement i think?
// TODO - System tray
