package com.example.jambo.services;

import java.io.File;
import java.util.*;

public class PlaylistService {
    private final Map<String, List<File>> playlists;
    private String currentPlaylistName;
    private boolean shuffleEnabled = false;
    private final Random random = new Random();

    public PlaylistService() {
        this.playlists = new HashMap<>();
        this.currentPlaylistName = "Default";
        this.playlists.put(currentPlaylistName, new ArrayList<>());
    }

    public void createPlaylist(String name) {
        if (!playlists.containsKey(name)) {
            playlists.put(name, new ArrayList<>());
        }
    }

    public void addSong(File songFile) {
        List<File> currentPlaylist = playlists.get(currentPlaylistName);
        if (!currentPlaylist.contains(songFile)) {
            currentPlaylist.add(songFile);
        }
    }

    public void removeSong(int index) {
        List<File> currentPlaylist = playlists.get(currentPlaylistName);
        if (index >= 0 && index < currentPlaylist.size()) {
            currentPlaylist.remove(index);
        }
    }

    public void clearPlaylist() {
        playlists.get(currentPlaylistName).clear();
    }

    public void toggleShuffle() {
        shuffleEnabled = !shuffleEnabled;
    }

    public int getNextSongIndex(int currentIndex) {
        List<File> currentPlaylist = playlists.get(currentPlaylistName);
        if (currentIndex >= 0 && !currentPlaylist.isEmpty()) {
            if (shuffleEnabled) {
                return random.nextInt(currentPlaylist.size());
            } else {
                return (currentIndex + 1) % currentPlaylist.size();
            }
        }
        return -1;
    }

    public int getPreviousSongIndex(int currentIndex) {
        List<File> currentPlaylist = playlists.get(currentPlaylistName);
        if (currentIndex >= 0 && !currentPlaylist.isEmpty()) {
            if (shuffleEnabled) {
                return random.nextInt(currentPlaylist.size());
            } else {
                return (currentIndex - 1 + currentPlaylist.size()) % currentPlaylist.size();
            }
        }
        return -1;
    }

    public File getSongFile(int index) {
        List<File> currentPlaylist = playlists.get(currentPlaylistName);
        if (index >= 0 && index < currentPlaylist.size()) {
            return currentPlaylist.get(index);
        }
        return null;
    }

    public List<File> getCurrentPlaylistSongs() {
        return playlists.get(currentPlaylistName);
    }
}