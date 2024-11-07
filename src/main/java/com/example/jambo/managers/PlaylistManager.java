package com.example.jambo.managers;

import com.example.jambo.Interfaces.PlaylistInterface;
import com.example.jambo.services.MetadataService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlaylistManager implements PlaylistInterface.PlaylistChangeListener {
    private final PlaylistInterface playlistService;
    private final ListView<String> songListView;
    private boolean isUpdating = false;


    public PlaylistManager(PlaylistInterface playlistService, ListView<String> songListView) {
        this.playlistService = playlistService;
        this.songListView = songListView;
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
            Platform.runLater(() -> {
                songListView.getItems().clear();
                ObservableList<String> items = FXCollections.observableArrayList();

                MetadataService metadataService = new MetadataService();
                for (File song : songs) {
                    try {
                        String formattedInfo = metadataService.formatSongMetadata(song);
                        items.add(formattedInfo);
                    } catch (Exception e) {
                        items.add(song.getName());
                        System.err.println("Error formatting metadata for " + song.getName() + ": " + e.getMessage());
                    }
                }
                songListView.setItems(items);
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
}