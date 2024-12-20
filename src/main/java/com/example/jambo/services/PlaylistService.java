package com.example.jambo.services;

import com.example.jambo.Interfaces.PlaylistInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Component

public class PlaylistService implements PlaylistInterface {
    private static final Logger logger = LoggerFactory.getLogger(PlaylistService.class);

    private final Map<String, List<File>> playlists = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> loadedPathsPerPlaylist = new ConcurrentHashMap<>();
    private final Set<PlaylistChangeListener> listeners = ConcurrentHashMap.newKeySet();
    private volatile String currentPlaylistName;
    private volatile boolean shuffleEnabled = false;
    private Random random;

    public PlaylistService() {
        this.currentPlaylistName = "Default";
        this.playlists.put(currentPlaylistName, Collections.synchronizedList(new ArrayList<>()));
        this.loadedPathsPerPlaylist.put(currentPlaylistName, ConcurrentHashMap.newKeySet());
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
    public void addPlaylistChangeListener(PlaylistChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void createPlaylist(String name) {
        if (!playlists.containsKey(name)) {
            playlists.put(name, new ArrayList<>());
            loadedPathsPerPlaylist.put(name, ConcurrentHashMap.newKeySet());
            logger.info("Created new playlist: {}", name);
            notifyPlaylistChanged(name);
        } else {
            logger.warn("Attempt to create duplicate playlist: {}", name);
        }
    }

    @Override
    public void deletePlaylist(String name) {
        if (!name.equals("Default") && playlists.containsKey(name)) {
            playlists.remove(name);
            loadedPathsPerPlaylist.remove(name);
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
            loadedPathsPerPlaylist.putIfAbsent(name, ConcurrentHashMap.newKeySet());
            notifyPlaylistChanged(name);
        }
    }

    @Override
    public void addSong(File songFile) {
        try {
            List<File> playlist = playlists.get(currentPlaylistName);
            if (playlist != null) {
                String newPath = songFile.getAbsolutePath();
                Set<String> loadedPathsForCurrentPlaylist = loadedPathsPerPlaylist.get(currentPlaylistName);
                if (loadedPathsForCurrentPlaylist != null && loadedPathsForCurrentPlaylist.add(newPath)) {
                    playlist.add(songFile);
                    notifyPlaylistChanged(currentPlaylistName);
                }
            }
        } catch (Exception e) {
            logger.error("Error adding song: {}", e.getMessage());
        }
    }

    @Override
    public void removeSong(int index) {
        List<File> playlist = playlists.get(currentPlaylistName);
        if (playlist != null && index >= 0 && index < playlist.size()) {
            File removedFile = playlist.remove(index);
            Set<String> loadedPathsForCurrentPlaylist = loadedPathsPerPlaylist.get(currentPlaylistName);
            if (loadedPathsForCurrentPlaylist != null) {
                loadedPathsForCurrentPlaylist.remove(removedFile.getAbsolutePath());
            }
            notifyPlaylistChanged(currentPlaylistName);
        } else {
            logger.warn("Attempted to remove invalid song index '{}' in playlist '{}'", index, currentPlaylistName);
        }
    }

    public void clearPlaylist(String playlistName) {
        List<File> playlist = playlists.get(playlistName);
        if (playlist != null) {
            playlist.clear();
            loadedPathsPerPlaylist.get(playlistName).clear();
            logger.info("Cleared playlist '{}'", playlistName);
            notifyPlaylistChanged(playlistName);
        } else {
            logger.warn("Attempted to clear non-existent playlist: {}", playlistName);
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
    public synchronized void toggleShuffle() {
        shuffleEnabled = !shuffleEnabled;
        logger.info("Shuffle mode set to '{}'", shuffleEnabled);
        if (shuffleEnabled && random == null) {
            random = new Random();
        }
    }

    @Override
    public int getNextSongIndex(int currentIndex) {
        List<File> currentPlaylist = playlists.get(currentPlaylistName);
        if (currentIndex >= 0 && !currentPlaylist.isEmpty()) {
            return shuffleEnabled ? random.nextInt(currentPlaylist.size())
                    : (currentIndex + 1) % currentPlaylist.size();
        }
        return -1;
    }

    @Override
    public int getPreviousSongIndex(int currentIndex) {
        List<File> currentPlaylist = playlists.get(currentPlaylistName);
        if (currentIndex >= 0 && !currentPlaylist.isEmpty()) {
            return shuffleEnabled ? random.nextInt(currentPlaylist.size())
                    : (currentIndex - 1 + currentPlaylist.size()) % currentPlaylist.size();
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
