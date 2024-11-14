package com.example.jambo.controllers;

import com.example.jambo.dependency.injection.DependencyContainer;
import com.example.jambo.managers.MusicPlayerManager;
import com.example.jambo.managers.PlaylistManager;
import com.example.jambo.managers.MetadataManager;
import com.example.jambo.ui.JamboUI;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.nio.file.*;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JamboController {
    private static final Logger logger = LoggerFactory.getLogger(JamboController.class);

    private final MusicPlayerManager musicPlayerManager;
    private final PlaylistManager playlistManager;
    private final MetadataManager metadataManager;
    private final JamboUI ui;
    private boolean isDragging = false;
    private Stage primaryStage;

    public JamboController(JamboUI ui) {
        this.ui = ui;
        try {
            DependencyContainer.initialize(ui.getVolumeSlider());
            this.musicPlayerManager = new MusicPlayerManager(
                    DependencyContainer.getMusicPlayerService(),
                    ui.getCurrentSongLabel(),
                    ui.getTimerLabel(),
                    ui.getProgressSlider()
            );
            this.playlistManager = new PlaylistManager(
                    DependencyContainer.getPlaylistService(),
                    ui.getSongListView()
            );
            this.metadataManager = new MetadataManager(
                    DependencyContainer.getMetadataService(),
                    ui.getFileInfoLabel(),
                    ui.getCurrentSongLabel(),
                    ui.getAlbumArtPlaceholder()
            );
            setupEventHandlers();
            logger.info("JamboController initialized successfully.");
        } catch (Exception e) {
            logger.error("Failed to initialize JamboController", e);
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
        this.primaryStage = primaryStage;
        configurePrimaryStage();
        loadSavedSongs();
        ui.initializeContextMenu(this);
    }

    private void configurePrimaryStage() {
        primaryStage.setTitle("Jambo - 0.3 - Default");
        primaryStage.setScene(ui.createScene(this));
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(800);
        primaryStage.setOnCloseRequest(event -> saveSongsToJson());
        primaryStage.show();
        logger.info("Primary stage configured and displayed.");
    }

    public void updateTitleWithPlaylistName(String playlistName) {
        String baseTitle = "Jambo - 0.3";
        String fullTitle = baseTitle + " - " + playlistName;
        if (primaryStage != null) {
            primaryStage.setTitle(fullTitle);
        }
    }

    public void playSelectedSong() {
        try {
            int selectedIndex = ui.getSongListView().getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                File songFile = playlistManager.getSongFile(selectedIndex);
                if (songFile != null && songFile.exists()) {
                    Media media = new Media(songFile.toURI().toString());
                    musicPlayerManager.playMedia(media);
                    logger.info("Playing selected song: {}", songFile.getName());

                    musicPlayerManager.setOnEndOfMedia(this::playNextSong);
                    metadataManager.updateFileInfo(songFile);
                } else {
                    logger.warn("Selected song file does not exist: {}", songFile);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to play selected song", e);
        }
    }

    public void loadSongs() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Music Directory");

        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            CompletableFuture.supplyAsync(() -> {
                try {
                    Set<String> existingPaths = playlistManager.getSongFiles().stream()
                            .map(File::getAbsolutePath)
                            .collect(Collectors.toSet());

                    try (Stream<Path> paths = Files.walk(selectedDirectory.toPath()).parallel()) {
                        return paths
                                .filter(path -> path.toString().toLowerCase().endsWith(".mp3"))
                                .map(Path::toFile)
                                .filter(file -> !existingPaths.contains(file.getAbsolutePath()))
                                .sorted(Comparator.comparing(File::getAbsolutePath))
                                .collect(Collectors.toList());
                    }
                } catch (IOException e) {
                    logger.error("Error scanning directory: {}", selectedDirectory, e);
                    return Collections.emptyList();
                }
            }).thenAccept(files -> Platform.runLater(() -> {
                for (Object file : files) {
                    addFileToPlaylist((File) file);
                }
                playlistManager.onPlaylistChanged(playlistManager.getCurrentPlaylistName(),
                        playlistManager.getSongFiles());
                saveSongsToJson();
            }));
        }
    }

    private void addFileToPlaylist(File file) {
        String formattedInfo;
        try {
            formattedInfo = metadataManager.formatSongMetadata(file);
        } catch (Exception e) {
            formattedInfo = file.getName();
        }
        playlistManager.addSong(file, formattedInfo);
    }

    private void loadSavedSongs() {
        playlistManager.loadSongsFromJson("saved_songs.json");
    }

    private void saveSongsToJson() {
        playlistManager.saveSongsToJson("saved_songs.json");
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