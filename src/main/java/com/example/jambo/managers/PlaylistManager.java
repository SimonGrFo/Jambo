package com.example.jambo.managers;

import com.example.jambo.services.PlaylistService;
import javafx.scene.control.ListView;
import java.io.File;
import java.util.List;

public class PlaylistManager {
    private final PlaylistService playlistService;
    private final ListView<String> songListView;

    public PlaylistManager(ListView<String> songListView) {
        this.songListView = songListView;
        this.playlistService = new PlaylistService();
    }

    public void createPlaylist(String name) {
        playlistService.createPlaylist(name);
    }

    public void deletePlaylist(String name) {
        playlistService.deletePlaylist(name);
    }

    public void switchPlaylist(String name) {
        playlistService.switchPlaylist(name);
        refreshPlaylistView();
    }

    public void addSong(File songFile, String formattedInfo) {
        playlistService.addSong(songFile);
        songListView.getItems().add(formattedInfo);
    }

    public void removeSong(int index) {
        playlistService.removeSong(index);
        songListView.getItems().remove(index);
    }

    public void clearPlaylist() {
        playlistService.clearPlaylist();
        songListView.getItems().clear();
    }

    private void refreshPlaylistView() {
        songListView.getItems().clear();
        for (File file : playlistService.getCurrentPlaylistSongs()) {
            songListView.getItems().add(file.getName());
        }
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

    public List<String> getPlaylistNames() {
        return playlistService.getPlaylistNames();
    }

    public String getCurrentPlaylistName() {
        return playlistService.getCurrentPlaylistName();
    }

    public boolean isDirectoryLoaded(File directory) {
        return playlistService.isDirectoryLoaded(directory);
    }

    public void addLoadedDirectory(File directory) {
        playlistService.addLoadedDirectory(directory);
    }
}
