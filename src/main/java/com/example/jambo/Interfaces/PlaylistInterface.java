package com.example.jambo.Interfaces;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface PlaylistInterface {
    void addPlaylistChangeListener(PlaylistChangeListener listener);
    void createPlaylist(String name);
    void deletePlaylist(String name);
    void switchToPlaylist(String name);
    Set<String> getPlaylistNames();
    String getCurrentPlaylistName();
    void addSong(File songFile);
    void removeSong(int index);
    void clearPlaylist();
    void toggleShuffle();
    int getNextSongIndex(int currentIndex);
    int getPreviousSongIndex(int currentIndex);
    File getSongFile(int index);
    List<File> getCurrentPlaylistSongs();
    List<File> getPlaylistSongs(String playlistName);

    interface PlaylistChangeListener {
        void onPlaylistChanged(String playlistName, List<File> songs);
        void onCurrentPlaylistChanged(String newPlaylistName);
    }
}
