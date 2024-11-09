package com.example.jambo.managers;

import com.example.jambo.Interfaces.PlaylistInterface;
import com.example.jambo.services.MetadataService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

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

public class PlaylistManager implements PlaylistInterface.PlaylistChangeListener {
    private final PlaylistInterface playlistService;
    private final ListView<String> songListView;
    private final MetadataService metadataService;
    private boolean isUpdating = false;

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

        try {
            isUpdating = true;
            List<String> formattedSongs = songs.parallelStream()
                    .map(song -> {
                        try {
                            return metadataService.formatSongMetadata(song);
                        } catch (Exception e) {
                            System.err.println("Error formatting metadata for " + song.getName() + ": " + e.getMessage());
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

    public void addSong(File songFile, String formattedInfo) {
        if (isUpdating) return;

        try {
            isUpdating = true;

            boolean isDuplicate = playlistService.getCurrentPlaylistSongs().stream()
                    .anyMatch(existingFile -> existingFile.getAbsolutePath().equals(songFile.getAbsolutePath()));

            if (!isDuplicate) {
                playlistService.addSong(songFile);
                Platform.runLater(() -> {
                    if (!songListView.getItems().contains(formattedInfo)) {
                        songListView.getItems().add(formattedInfo);
                    }
                });
            }
        } finally {
            isUpdating = false;
        }
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
                        .collect(Collectors.toList());
                Platform.runLater(() -> {
                    validFiles.forEach(file -> addSong(file, file.getName()));
                });
            }
        } catch (Exception e) {
            System.err.println("Error loading saved songs: " + e.getMessage());
        }
    }

    public void saveSongsToJson(String filename) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(filename)) {
            List<String> songPaths = getSongFiles().stream()
                    .map(File::getAbsolutePath)
                    .collect(Collectors.toList());
            gson.toJson(songPaths, writer);
        } catch (Exception e) {
            System.err.println("Error saving songs: " + e.getMessage());
        }
    }
}