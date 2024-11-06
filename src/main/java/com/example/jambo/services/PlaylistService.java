package com.example.jambo.services;

import com.example.jambo.Interfaces.PlaylistInterface;

import java.io.File;
import java.util.*;

public class PlaylistService implements PlaylistInterface {
    private final Map<String, List<File>> playlists;
    private String currentPlaylistName;
    private boolean shuffleEnabled = false;
    private final Random random = new Random();
    private final Set<PlaylistChangeListener> listeners = new HashSet<>();

    public PlaylistService() {
        this.playlists = new HashMap<>();
        this.currentPlaylistName = "Default";
        this.playlists.put(currentPlaylistName, new ArrayList<>());
    }

    @Override
    public void addPlaylistChangeListener(PlaylistChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyPlaylistChanged(String playlistName) {
        for (PlaylistChangeListener listener : listeners) {
            listener.onPlaylistChanged(playlistName, playlists.get(playlistName));
            if (playlistName.equals(currentPlaylistName)) {
                listener.onCurrentPlaylistChanged(currentPlaylistName);
            }
        }
    }

    @Override
    public void createPlaylist(String name) {
        if (!playlists.containsKey(name)) {
            playlists.put(name, new ArrayList<>());
            notifyPlaylistChanged(name);
        }
    }

    @Override
    public void deletePlaylist(String name) {
        if (!name.equals("Default") && playlists.containsKey(name)) {
            playlists.remove(name);
            if (currentPlaylistName.equals(name)) {
                switchToPlaylist("Default");
            }
            notifyPlaylistChanged(name);
        }
    }

    @Override
    public void switchToPlaylist(String name) {
        if (playlists.containsKey(name)) {
            currentPlaylistName = name;
            notifyPlaylistChanged(name);
        }
    }

    public void addSongToPlaylist(String playlistName, File songFile) {
        try {
            List<File> playlist = playlists.get(playlistName);
            if (playlist != null && !playlist.contains(songFile)) {
                playlist.add(songFile);
                notifyPlaylistChanged(playlistName);
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void addSong(File songFile) {
        addSongToPlaylist(currentPlaylistName, songFile);
    }

    public void removeSongFromPlaylist(String playlistName, int index) {
        try {
            List<File> playlist = playlists.get(playlistName);
            if (playlist != null && index >= 0 && index < playlist.size()) {
                playlist.remove(index);
                notifyPlaylistChanged(playlistName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
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

    @Override
    public void clearPlaylist() {
        clearPlaylist(currentPlaylistName);
    }

    @Override
    public Set<String> getPlaylistNames() {
        return new HashSet<>(playlists.keySet());
    }

    @Override
    public String getCurrentPlaylistName() {
        return currentPlaylistName;
    }

    @Override
    public List<File> getPlaylistSongs(String playlistName) {
        return new ArrayList<>(playlists.getOrDefault(playlistName, new ArrayList<>()));
    }

    @Override
    public void toggleShuffle() {
        shuffleEnabled = !shuffleEnabled;
    }

    @Override
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

    @Override
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

    @Override
    public File getSongFile(int index) {
        List<File> currentPlaylist = playlists.get(currentPlaylistName);
        if (index >= 0 && index < currentPlaylist.size()) {
            return currentPlaylist.get(index);
        }
        return null;
    }

    @Override
    public List<File> getCurrentPlaylistSongs() {
        return playlists.get(currentPlaylistName);
    }
}