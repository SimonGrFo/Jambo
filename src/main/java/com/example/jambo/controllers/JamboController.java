package com.example.jambo.controllers;

import com.example.jambo.ui.JamboUI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.media.Media;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JamboController {
    private final PlayerController playerController;
    private final PlaylistController playlistController;
    private final MetadataController metadataController;
    private final JamboUI ui;
    private boolean isDragging = false;

    public JamboController(JamboUI ui) {
        this.ui = ui;
        this.playerController = new PlayerController(
                ui.getCurrentSongLabel(),
                ui.getTimerLabel(),
                ui.getProgressSlider(),
                ui.getVolumeSlider()
        );
        this.playlistController = new PlaylistController(ui.getSongListView());
        this.metadataController = new MetadataController(
                ui.getFileInfoLabel(),
                ui.getCurrentSongLabel(),
                ui.getAlbumArtView()
        );

        setupEventHandlers();
    }

    private void setupEventHandlers() {
        ui.getSongListView().setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                playSelectedSong();
            }
        });

        ui.getProgressSlider().setOnMousePressed(e -> isDragging = true);
        ui.getProgressSlider().setOnMouseReleased(e -> {
            if (isDragging) {
                double newTime = ui.getProgressSlider().getValue() *
                        playerController.getMediaPlayer().getTotalDuration().toSeconds();
                playerController.seekTo(newTime);
                isDragging = false;
            }
        });
    }

    public void initializeStage(Stage primaryStage) {
        loadSavedSongs();

        primaryStage.setTitle("Jambo - 0.2");
        primaryStage.setScene(ui.createScene(this));
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(800);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> saveSongsToJson());
    }

    public void playSelectedSong() {
        int selectedIndex = ui.getSongListView().getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            File songFile = playlistController.getSongFile(selectedIndex);
            if (songFile != null) {
                Media media = new Media(songFile.toURI().toString());
                playerController.playMedia(media);
                metadataController.updateFileInfo(songFile);
            }
        }
    }

    public void loadSongs() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null && !playlistController.isDirectoryLoaded(selectedDirectory)) {
            playlistController.addLoadedDirectory(selectedDirectory);

            File[] files = selectedDirectory.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".mp3"));

            if (files != null) {
                for (File file : files) {
                    try {
                        String formattedInfo = metadataController.formatSongMetadata(file);
                        playlistController.addSong(file, formattedInfo);
                    } catch (Exception e) {
                        System.err.println("Error reading metadata: " + e.getMessage());
                        playlistController.addSong(file, file.getName());
                    }
                }
                saveSongsToJson();
            }
        }
    }

    private void loadSavedSongs() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("saved_songs.json")) {
            Type listType = new TypeToken<ArrayList<String>>(){}.getType();
            List<String> songPaths = gson.fromJson(reader, listType);

            for (String path : songPaths) {
                File songFile = new File(path);
                if (songFile.exists()) {
                    try {
                        String formattedInfo = metadataController.formatSongMetadata(songFile);
                        playlistController.addSong(songFile, formattedInfo);
                    } catch (Exception e) {
                        System.err.println("Error reading metadata: " + e.getMessage());
                        playlistController.addSong(songFile, songFile.getName());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading songs from JSON: " + e.getMessage());
        }
    }

    private void saveSongsToJson() {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("saved_songs.json")) {
            List<String> songPaths = new ArrayList<>();
            for (File file : playlistController.getSongFiles()) {
                songPaths.add(file.getAbsolutePath());
            }
            gson.toJson(songPaths, writer);
        } catch (Exception e) {
            System.err.println("Error saving songs: " + e.getMessage());
        }
    }

    public void clearSongs() {
        playlistController.clearPlaylist();
        playerController.stopMusic();
    }

    public void pauseMusic() {
        playerController.pauseMusic();
    }

    public void stopMusic() {
        playerController.stopMusic();
    }

    public void playNextSong() {
        int nextIndex = playlistController.getNextSongIndex();
        if (nextIndex >= 0) {
            ui.getSongListView().getSelectionModel().select(nextIndex);
            playSelectedSong();
        }
    }

    public void playPreviousSong() {
        int previousIndex = playlistController.getPreviousSongIndex();
        if (previousIndex >= 0) {
            ui.getSongListView().getSelectionModel().select(previousIndex);
            playSelectedSong();
        }
    }

    public void toggleShuffle() {
        playlistController.toggleShuffle();
    }

    public void toggleLoop() {
        playerController.toggleLoop();
    }

    public void toggleMute() {
        playerController.toggleMute();
    }
}
