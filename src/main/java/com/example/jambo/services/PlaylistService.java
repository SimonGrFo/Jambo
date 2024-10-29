package com.example.jambo.services;

import java.io.File;
import java.util.*;

public class PlaylistService {
    private final Map<String, List<File>> playlists;
    private String currentPlaylistName;
    private boolean shuffleEnabled = false;
    private final Random random = new Random();
    private final Set<PlaylistChangeListener> listeners = new HashSet<>();

    public interface PlaylistChangeListener {
        void onPlaylistChanged(String playlistName, List<File> songs);
        void onCurrentPlaylistChanged(String newPlaylistName);
    }

    public PlaylistService() {
        this.playlists = new HashMap<>();
        this.currentPlaylistName = "Default";
        this.playlists.put(currentPlaylistName, new ArrayList<>());
    }

    public void addPlaylistChangeListener(PlaylistChangeListener listener) {
        listeners.add(listener);
    }

    public void removePlaylistChangeListener(PlaylistChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyPlaylistChanged(String playlistName) {
        for (PlaylistChangeListener listener : listeners) {
            listener.onPlaylistChanged(playlistName, playlists.get(playlistName));
            if (playlistName.equals(currentPlaylistName)) {
                listener.onCurrentPlaylistChanged(currentPlaylistName);
            }
        }
    }

    public void createPlaylist(String name) {
        if (!playlists.containsKey(name)) {
            playlists.put(name, new ArrayList<>());
            notifyPlaylistChanged(name);
        }
    }

    public void deletePlaylist(String name) {
        if (!name.equals("Default") && playlists.containsKey(name)) {
            playlists.remove(name);
            if (currentPlaylistName.equals(name)) {
                switchToPlaylist("Default");
            }
            notifyPlaylistChanged(name);
        }
    }

    public void renamePlaylist(String oldName, String newName) {
        if (!oldName.equals("Default") && playlists.containsKey(oldName) && !playlists.containsKey(newName)) {
            List<File> songs = playlists.remove(oldName);
            playlists.put(newName, songs);
            if (currentPlaylistName.equals(oldName)) {
                currentPlaylistName = newName;
            }
            notifyPlaylistChanged(newName);
        }
    }

    public void switchToPlaylist(String name) {
        if (playlists.containsKey(name)) {
            currentPlaylistName = name;
            notifyPlaylistChanged(name);
        }
    }

    public void addSongToPlaylist(String playlistName, File songFile) {
        List<File> playlist = playlists.get(playlistName);
        if (playlist != null && !playlist.contains(songFile)) {
            playlist.add(songFile);
            notifyPlaylistChanged(playlistName);
        }
    }

    public void addSong(File songFile) {
        addSongToPlaylist(currentPlaylistName, songFile);
    }

    public void removeSongFromPlaylist(String playlistName, int index) {
        List<File> playlist = playlists.get(playlistName);
        if (playlist != null && index >= 0 && index < playlist.size()) {
            playlist.remove(index);
            notifyPlaylistChanged(playlistName);
        }
    }

    public void removeSong(int index) {
        removeSongFromPlaylist(currentPlaylistName, index);
    }

    public void clearPlaylist(String playlistName) {
        List<File> playlist = playlists.get(playlistName);
        if (playlist != null) {
            playlist.clear();
            notifyPlaylistChanged(playlistName);
        }
    }

    public void clearPlaylist() {
        clearPlaylist(currentPlaylistName);
    }

    public Set<String> getPlaylistNames() {
        return new HashSet<>(playlists.keySet());
    }

    public String getCurrentPlaylistName() {
        return currentPlaylistName;
    }

    public List<File> getPlaylistSongs(String playlistName) {
        return new ArrayList<>(playlists.getOrDefault(playlistName, new ArrayList<>()));
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