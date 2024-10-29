package com.example.jambo.managers;

import com.example.jambo.services.MetadataService;
import com.example.jambo.services.PlaylistService;
import javafx.scene.control.ListView;
import java.io.File;
import java.util.List;
import java.util.Set;

public class PlaylistManager implements PlaylistService.PlaylistChangeListener {
    private final PlaylistService playlistService;
    private final ListView<String> songListView;

    public PlaylistManager(ListView<String> songListView) {
        this.songListView = songListView;
        this.playlistService = new PlaylistService();
        this.playlistService.addPlaylistChangeListener(this);
    }

    @Override
    public void onPlaylistChanged(String playlistName, List<File> songs) {
        if (playlistName.equals(playlistService.getCurrentPlaylistName())) {
            updateSongListView(songs);
        }
    }

    @Override
    public void onCurrentPlaylistChanged(String newPlaylistName) {
        updateSongListView(playlistService.getPlaylistSongs(newPlaylistName));
    }

    private void updateSongListView(List<File> songs) {
        songListView.getItems().clear();
        for (File song : songs) {
            try {
                String formattedInfo = new MetadataService().formatSongMetadata(song);
                songListView.getItems().add(formattedInfo);
            } catch (Exception e) {
                songListView.getItems().add(song.getName());
            }
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