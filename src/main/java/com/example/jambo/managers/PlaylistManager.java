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