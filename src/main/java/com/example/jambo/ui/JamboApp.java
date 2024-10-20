package com.example.jambo.ui;

import com.example.jambo.player.MusicPlayer;
import com.example.jambo.model.Track;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JamboApp extends Application {

    private static final String TRACKS_FILE = "tracks.json";
    private MusicPlayer musicPlayer;
    private ObservableList<Track> tracks;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Jambo 0.1");

        tracks = FXCollections.observableArrayList();

        ListView<Track> trackListView = new ListView<>(tracks);
        trackListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Track track, boolean empty) {
                super.updateItem(track, empty);
                if (empty || track == null) {
                    setText(null);
                } else {
                    setText(track.name());
                }
            }
        });

        Button loadFolderButton = new Button("Load Folder");   //TODO - BROKEN! IT DOESN'T WORK!
        Button stopButton = new Button("Stop");
        //TODO - MORE BUTTONS; PAUSE PLAY ETC


        loadFolderButton.setOnAction(e -> {
            Task<List<Track>> loadTask = new Task<>() {
                @Override
                protected List<Track> call() throws Exception {
                    return openFolderAndLoadFiles(primaryStage);
                }
            };

            loadTask.setOnSucceeded(event -> {
                List<Track> loadedTracks = loadTask.getValue();
                if (!loadedTracks.isEmpty()) {
                    tracks.setAll(loadedTracks);
                    if (musicPlayer == null) {
                        musicPlayer = new MusicPlayer(tracks);
                    } else {
                        musicPlayer.updateTracks(tracks);
                    }
                    saveTracksToFile(loadedTracks);
                }
            });

            new Thread(loadTask).start();
        });

        stopButton.setOnAction(e -> {
            if (musicPlayer != null) {
                musicPlayer.stop();
            }
        });

        trackListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Track selectedTrack = trackListView.getSelectionModel().getSelectedItem();
                if (musicPlayer != null && selectedTrack != null) {
                    int selectedIndex = trackListView.getSelectionModel().getSelectedIndex();
                    musicPlayer.playTrack(selectedIndex);
                }
            }
        });

        BorderPane root = new BorderPane();
        root.setCenter(trackListView);

        HBox controlButtons = new HBox(10);
        controlButtons.getChildren().addAll(loadFolderButton, stopButton);
        root.setBottom(controlButtons);

        Scene scene = new Scene(root, 600, 400);

        try {
            String cssPath = Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (NullPointerException ex) {
            System.out.println("CSS file not found. Skipping stylesheet loading.");
        }

        primaryStage.setScene(scene);
        primaryStage.show();

        loadTracksFromFile();
    }

    private List<Track> openFolderAndLoadFiles(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Music Folder");

        File selectedDirectory = directoryChooser.showDialog(stage);
        List<Track> mp3Tracks = new ArrayList<>();

        if (selectedDirectory != null) {
            File[] files = selectedDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
            if (files != null) {
                for (File file : files) {
                    mp3Tracks.add(new Track(file.getName(), file.getAbsolutePath()));
                }
            }
        }
        return mp3Tracks;
    }

    private void saveTracksToFile(List<Track> tracks) {
        JSONArray jsonTracks = new JSONArray();
        for (Track track : tracks) {
            jsonTracks.put(track.path());
        }
        try {
            Files.write(Paths.get(TRACKS_FILE), jsonTracks.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTracksFromFile() {
        try {
            if (Files.exists(Paths.get(TRACKS_FILE))) {
                String content = new String(Files.readAllBytes(Paths.get(TRACKS_FILE)));
                JSONArray jsonTracks = new JSONArray(content);
                List<Track> loadedTracks = new ArrayList<>();

                for (int i = 0; i < jsonTracks.length(); i++) {
                    String path = jsonTracks.getString(i);
                    File file = new File(path);
                    if (file.exists()) {
                        loadedTracks.add(new Track(file.getName(), path));
                    } else {
                        System.out.println("File does not exist: " + path);
                    }
                }

                if (!loadedTracks.isEmpty()) {
                    tracks.setAll(loadedTracks);
                    musicPlayer = new MusicPlayer(tracks);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
