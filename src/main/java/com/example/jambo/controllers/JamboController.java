package com.example.jambo.controllers;

import com.example.jambo.di.DependencyContainer;
import com.example.jambo.managers.MusicPlayerManager;
import com.example.jambo.managers.PlaylistManager;
import com.example.jambo.managers.MetadataManager;
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
    private final MusicPlayerManager musicPlayerManager;
    private final PlaylistManager playlistManager;
    private final MetadataManager metadataManager;
    private final JamboUI ui;
    private boolean isDragging = false;

    public JamboController(JamboUI ui) {
        this.ui = ui;

        try {
            DependencyContainer.initialize(ui.getVolumeSlider());

            this.musicPlayerManager = new MusicPlayerManager(
                    DependencyContainer.getMusicPlayerService(),
                    ui.getCurrentSongLabel(),
                    ui.getTimerLabel(),
                    ui.getProgressSlider(),
                    ui.getVolumeSlider()
            );

            this.playlistManager = new PlaylistManager(
                    DependencyContainer.getPlaylistService(),
                    ui.getSongListView()
            );

            this.metadataManager = new MetadataManager(
                    DependencyContainer.getMetadataService(),
                    ui.getFileInfoLabel(),
                    ui.getCurrentSongLabel()
            );

            setupEventHandlers();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize JamboController", e);
        }
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
                try {
                    double newTime = ui.getProgressSlider().getValue() * musicPlayerManager.getTotalDuration();
                    musicPlayerManager.seekTo(newTime);
                } finally {
                    isDragging = false;
                }
            }
        });
    }

    public void initializeStage(Stage primaryStage) {
        loadSavedSongs();

        primaryStage.setTitle("Jambo - 0.2");
        primaryStage.setScene(ui.createScene(this));
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(800);

        ui.initializeContextMenu(this);

        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> saveSongsToJson());
    }

    public void playSelectedSong() {
        try {
            int selectedIndex = ui.getSongListView().getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                File songFile = playlistManager.getSongFile(selectedIndex);
                if (songFile != null && songFile.exists()) {
                    Media media = new Media(songFile.toURI().toString());
                    musicPlayerManager.playMedia(media);

                    // Set the onEndOfMedia callback to play the next song
                    musicPlayerManager.setOnEndOfMedia(() -> playNextSong());

                    metadataManager.updateFileInfo(songFile);
                }
            }
        } catch (Exception e) {
            // Handle the exception silently or log it
        }
    }

    public void loadSongs() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            File[] files = selectedDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));

            if (files != null && files.length > 0) {
                for (File file : files) {
                    try {
                        String formattedInfo = metadataManager.formatSongMetadata(file);
                        playlistManager.addSong(file, formattedInfo);
                    } catch (Exception e) {
                        playlistManager.addSong(file, file.getName());
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
                        String formattedInfo = metadataManager.formatSongMetadata(songFile);
                        playlistManager.addSong(songFile, formattedInfo);
                    } catch (Exception e) {
                        // Handling the exception silently without logging
                    }
                }
            }

        } catch (Exception e) {
            // Handling the exception silently without logging
        }
    }

    public void saveSongsToJson() {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("saved_songs.json")) {
            List<String> songPaths = new ArrayList<>();
            for (File file : playlistManager.getSongFiles()) {
                songPaths.add(file.getAbsolutePath());
            }
            gson.toJson(songPaths, writer);
        } catch (Exception e) {
            // Handling the exception silently without logging
        }
    }

    public void clearSongs() {
        playlistManager.clearPlaylist();
        musicPlayerManager.stopMusic();
    }

    public PlaylistManager getPlaylistManager() {
        return playlistManager;
    }

    public void pauseMusic() {
        musicPlayerManager.pauseMusic();
    }

    public void stopMusic() {
        musicPlayerManager.stopMusic();
    }

    public void playNextSong() {
        int nextIndex = playlistManager.getNextSongIndex();
        if (nextIndex >= 0) {
            ui.getSongListView().getSelectionModel().select(nextIndex);
            playSelectedSong();
        }
    }

    public void playPreviousSong() {
        int previousIndex = playlistManager.getPreviousSongIndex();
        if (previousIndex >= 0) {
            ui.getSongListView().getSelectionModel().select(previousIndex);
            playSelectedSong();
        }
    }

    public void toggleShuffle() {
        playlistManager.toggleShuffle();
    }

    public void toggleLoop() {
        musicPlayerManager.toggleLoop();
    }

    public void toggleMute() {
        musicPlayerManager.toggleMute();
    }

    public void removeSong(int index) {
        if (index >= 0) {
            playlistManager.removeSong(index);
        }
    }

    public File getSongFile(int index) {
        return playlistManager.getSongFile(index);
    }
}
