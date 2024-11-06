package com.example.jambo.Interface;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface IPlaylistManager {
    void createPlaylist(String name);
    void deletePlaylist(String name);
    void switchToPlaylist(String name);
    Set<String> getPlaylistNames();
    String getCurrentPlaylistName();
    void addSong(File songFile, String formattedInfo);
    void removeSong(int index);
    void clearPlaylist();
    void toggleShuffle();
    int getNextSongIndex();
    int getPreviousSongIndex();
    File getSongFile(int index);
    List<File> getSongFiles();
}