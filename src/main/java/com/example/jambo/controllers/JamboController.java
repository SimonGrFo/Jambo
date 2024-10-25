package com.example.jambo.controllers;

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
        this.musicPlayerManager = new MusicPlayerManager(
                ui.getCurrentSongLabel(),
                ui.getTimerLabel(),
                ui.getProgressSlider(),
                ui.getVolumeSlider()
        );
        this.playlistManager = new PlaylistManager(ui.getSongListView());
        this.metadataManager = new MetadataManager(
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
                double newTime = ui.getProgressSlider().getValue() * musicPlayerManager.getTotalDuration();
                musicPlayerManager.seekTo(newTime);
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
            File songFile = playlistManager.getSongFile(selectedIndex);
            if (songFile != null) {
                Media media = new Media(songFile.toURI().toString());
                musicPlayerManager.playMedia(media);
                metadataManager.updateFileInfo(songFile);
            }
        }
    }

    public void loadSongs() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null && !playlistManager.isDirectoryLoaded(selectedDirectory)) {
            playlistManager.addLoadedDirectory(selectedDirectory);

            File[] files = selectedDirectory.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".mp3"));

            if (files != null) {
                for (File file : files) {
                    try {
                        String formattedInfo = metadataManager.formatSongMetadata(file);
                        playlistManager.addSong(file, formattedInfo);
                    } catch (Exception e) {
                        System.err.println("Error reading metadata: " + e.getMessage());
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
                        System.err.println("Error reading metadata: " + e.getMessage());
                        playlistManager.addSong(songFile, songFile.getName());
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
            for (File file : playlistManager.getSongFiles()) {
                songPaths.add(file.getAbsolutePath());
            }
            gson.toJson(songPaths, writer);
        } catch (Exception e) {
            System.err.println("Error saving songs: " + e.getMessage());
        }
    }

    public void clearSongs() {
        playlistManager.clearPlaylist();
        musicPlayerManager.stopMusic();
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
}