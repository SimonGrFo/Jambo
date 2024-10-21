package com.example.jambo.ui;

import com.example.jambo.player.MusicPlayer;
import com.example.jambo.model.Track;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JamboApplication extends Application {

    private static final String TRACKS_FILE = "tracks.json";
    private MusicPlayer musicPlayer;
    private ObservableList<Track> tracks;
    private ProgressBar progressBar;
    private Label currentTrackLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Jambo 0.1");

        tracks = FXCollections.observableArrayList();

        ListView<Track> trackListView = new ListView<>(tracks);
        trackListView.setCellFactory(param -> new TrackListCell());

        Button loadFolderButton = new Button("Load Folder");
        Button stopButton = new Button("Stop");
        Button playButton = new Button("Play");     //TODO -
        Button pauseButton = new Button("Pause");   //TODO - these are both super screwy and do weird things
        Button previousButton = new Button("Previous");
        Button nextButton = new Button("Next");
        Button shuffleButton = new Button("Shuffle");

        progressBar = new ProgressBar(0);
        currentTrackLabel = new Label("Currently Playing: None");

        loadFolderButton.setOnAction(e -> loadTracks(primaryStage));
        stopButton.setOnAction(e -> stopMusic());
        playButton.setOnAction(e -> playSelectedTrack(trackListView));
        pauseButton.setOnAction(e -> pauseOrResumeMusic());
        previousButton.setOnAction(e -> musicPlayer.previousTrack());
        nextButton.setOnAction(e -> musicPlayer.nextTrack());
        shuffleButton.setOnAction(e -> musicPlayer.shuffleTracks());

        BorderPane root = new BorderPane();
        root.setCenter(trackListView);

        HBox controlButtons = new HBox(10);
        controlButtons.getChildren().addAll(loadFolderButton, playButton, pauseButton, stopButton, previousButton, nextButton, shuffleButton);
        root.setBottom(controlButtons);

        VBox statusBox = new VBox(10);
        statusBox.getChildren().addAll(currentTrackLabel, progressBar);
        root.setTop(statusBox);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        loadTracksFromFile();
    }

    private void playSelectedTrack(ListView<Track> trackListView) {
        Track selectedTrack = trackListView.getSelectionModel().getSelectedItem();
        if (musicPlayer != null && selectedTrack != null) {
            int selectedIndex = trackListView.getSelectionModel().getSelectedIndex();
            currentTrackLabel.setText("Currently Playing: " + selectedTrack.name());
            musicPlayer.playTrack(selectedIndex);
            startProgressUpdate();
        }
    }

    private void startProgressUpdate() {
        Task<Void> progressTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (musicPlayer != null && !musicPlayer.isPaused()) {
                    double progress = musicPlayer.getCurrentProgress();
                    updateProgress(progress, 1);
                    Thread.sleep(500);
                }
                return null;
            }

            @Override
            protected void updateProgress(double workDone, double max) {
                super.updateProgress(workDone, max);
                progressBar.setProgress(workDone);
            }
        };

        new Thread(progressTask).start();
    }

    private void pauseOrResumeMusic() {
        if (musicPlayer != null) {
            if (musicPlayer.isPaused()) {
                musicPlayer.resume();
            } else {
                musicPlayer.pause();
            }
        }
    }

    private void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            currentTrackLabel.setText("Currently Playing: None");
            progressBar.setProgress(0);
        }
    }

    private void loadTracks(Stage primaryStage) {
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
    }

    private List<Track> openFolderAndLoadFiles(Stage primaryStage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Music Folder");

        File selectedDirectory = directoryChooser.showDialog(primaryStage);
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
