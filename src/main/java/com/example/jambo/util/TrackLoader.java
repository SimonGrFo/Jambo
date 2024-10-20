package com.example.jambo.util;

import com.example.jambo.model.Track;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.concurrent.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TrackLoader {

    public static final String TRACKS_FILE = "tracks.json";
    private final Stage stage;

    public TrackLoader(Stage stage) {
        this.stage = stage;
    }

    public void loadTracks(Consumer<List<Track>> callback) {
        Task<List<Track>> loadTask = new Task<>() {
            @Override
            protected List<Track> call() throws Exception {
                return openFolderAndLoadFiles();
            }
        };

        loadTask.setOnSucceeded(event -> callback.accept(loadTask.getValue()));
        new Thread(loadTask).start();
    }

    private List<Track> openFolderAndLoadFiles() {
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
}
