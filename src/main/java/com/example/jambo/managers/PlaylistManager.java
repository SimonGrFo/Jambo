package com.example.jambo.managers;

import com.example.jambo.Interfaces.PlaylistInterface;
import com.example.jambo.services.MetadataService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class PlaylistManager implements PlaylistInterface.PlaylistChangeListener {
    private final PlaylistInterface playlistService;
    private final ListView<String> songListView;
    private final MetadataService metadataService;
    private boolean isUpdating = false;
    private static final Logger logger = LoggerFactory.getLogger(PlaylistManager.class);

    public PlaylistManager(PlaylistInterface playlistService, ListView<String> songListView) {
        this.playlistService = playlistService;
        this.songListView = songListView;
        this.metadataService = new MetadataService();
        this.playlistService.addPlaylistChangeListener(this);
    }

    @Override
    public void onPlaylistChanged(String playlistName, List<File> songs) {
        if (playlistName.equals(playlistService.getCurrentPlaylistName())) {
            updateSongListView(new ArrayList<>(songs));
        }
    }

    @Override
    public void onCurrentPlaylistChanged(String newPlaylistName) {
        updateSongListView(playlistService.getPlaylistSongs(newPlaylistName));
    }

    private void updateSongListView(List<File> songs) {
        if (isUpdating) return;

        isUpdating = true;
        try {
            List<String> formattedSongs = songs.parallelStream()
                    .map(song -> {
                        try {
                            return metadataService.formatSongMetadata(song);
                        } catch (Exception e) {
                            logger.warn("Failed to format metadata for {}, using filename", song.getName());
                            return song.getName();
                        }
                    })
                    .collect(Collectors.toList());
            Platform.runLater(() -> {
                songListView.getItems().clear();
                songListView.setItems(FXCollections.observableArrayList(formattedSongs));
            });
        } finally {
            isUpdating = false;
        }
    }

    public void createPlaylist(String name) {
        playlistService.createPlaylist(name);
    }

    public void deletePlaylist(String name) {
        playlistService.deletePlaylist(name);
    }

    public void switchToPlaylist(String name) {
        playlistService.switchToPlaylist(name);
    }

    public Set<String> getPlaylistNames() {
        return playlistService.getPlaylistNames();
    }

    public String getCurrentPlaylistName() {
        return playlistService.getCurrentPlaylistName();
    }

    @Override
    public void addSong(File songFile, String formattedInfo) {
        if (isUpdating) return;

        isUpdating = true;
        try {
            if (!isDuplicateSong(songFile)) {
                playlistService.addSong(songFile);
                Platform.runLater(() -> {
                    if (!songListView.getItems().contains(formattedInfo)) {
                        songListView.getItems().add(formattedInfo);
                    }
                });
                logger.info("Added song: {}", songFile.getName());
            }
        } catch (Exception e) {
            logger.error("Failed to add song {}: {}", songFile.getName(), e.getMessage());
            throw e;
        } finally {
            isUpdating = false;
        }
    }

    private boolean isDuplicateSong(File songFile) {
        boolean isDuplicate = playlistService.getCurrentPlaylistSongs().stream()
                .anyMatch(existingFile -> existingFile.getAbsolutePath().equals(songFile.getAbsolutePath()));

        if (isDuplicate) {
            logger.warn("Skipping duplicate song: {}", songFile.getName());
        }
        return isDuplicate;
    }

    public void removeSong(int index) {
        playlistService.removeSong(index);
        songListView.getItems().remove(index);
    }

    public void clearPlaylist() {
        playlistService.clearPlaylist();
        songListView.getItems().clear();
    }

    public void toggleShuffle() {
        playlistService.toggleShuffle();
    }

    public int getNextSongIndex() {
        return playlistService.getNextSongIndex(
                songListView.getSelectionModel().getSelectedIndex()
        );
    }

    public int getPreviousSongIndex() {
        return playlistService.getPreviousSongIndex(
                songListView.getSelectionModel().getSelectedIndex()
        );
    }

    public File getSongFile(int index) {
        return playlistService.getSongFile(index);
    }

    public List<File> getSongFiles() {
        return playlistService.getCurrentPlaylistSongs();
    }

    public void loadSongsFromJson(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            Type listType = new TypeToken<ArrayList<String>>(){}.getType();
            List<String> songPaths = new Gson().fromJson(reader, listType);

            if (songPaths != null) {
                List<File> validFiles = songPaths.parallelStream()
                        .map(File::new)
                        .filter(File::exists)
                        .sorted(Comparator.comparing(File::getAbsolutePath))
                        .toList();

                logger.info("Loading {} valid songs from {}", validFiles.size(), filename);

                Platform.runLater(() -> {
                    validFiles.forEach(file -> {
                        try {
                            String formattedMetadata = metadataService.formatSongMetadata(file);
                            addSong(file, formattedMetadata);
                        } catch (Exception e) {
                            logger.warn("Using filename for {} due to metadata error", file.getName());
                            addSong(file, file.getName());
                        }
                    });
                    logger.info("{} songs loaded successfully", validFiles.size());
                });
            }
        } catch (Exception e) {
            logger.error("Failed to load songs from {}: {}", filename, e.getMessage());
        }
    }

    public void saveSongsToJson(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            List<String> songPaths = getSongFiles().stream()
                    .map(File::getAbsolutePath)
                    .collect(Collectors.toList());
            new Gson().toJson(songPaths, writer);
            logger.info("Saved {} songs to {}", songPaths.size(), filename);
        } catch (Exception e) {
            logger.error("Failed to save songs to {}: {}", filename, e.getMessage());
        }
    }
}