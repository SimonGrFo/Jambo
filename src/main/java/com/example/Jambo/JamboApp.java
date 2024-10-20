package com.example.Jambo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.FileInputStream;
import java.io.IOException;

public class JamboApp extends Application {

    private AdvancedPlayer player;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Music Player");

        Button playButton = new Button("Play Music");
        playButton.setOnAction(e -> {
            String filePath = "D:\\MUSIC\\Death Grips\\Exmilitary\\Death Grips - Exmilitary - 3 - Spread Eagle Cross the Block.mp3";
            playMusic(filePath);
        });

        StackPane root = new StackPane();
        root.getChildren().add(playButton);

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void playMusic(String filePath) {
        stopMusic();

        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            player = new AdvancedPlayer(fileInputStream);
            new Thread(() -> {
                try {
                    player.play();
                } catch (JavaLayerException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (JavaLayerException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void stopMusic() {
        if (player != null) {
            player.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
