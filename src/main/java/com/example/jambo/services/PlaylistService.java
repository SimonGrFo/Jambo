package com.example.jambo.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaylistService {
    private final List<File> songFiles;
    private final List<File> loadedDirectories;
    private boolean shuffleEnabled = false;
    private final Random random = new Random();

    public PlaylistService() {
        this.songFiles = new ArrayList<>();
        this.loadedDirectories = new ArrayList<>();
    }

    public void addSong(File songFile) {
        if (!songFiles.contains(songFile)) {
            songFiles.add(songFile);
        }
    }

    public void clearPlaylist() {
        songFiles.clear();
    }

    public void toggleShuffle() {
        shuffleEnabled = !shuffleEnabled;
    }

    public int getNextSongIndex(int currentIndex) {
        if (currentIndex >= 0) {
            if (shuffleEnabled) {
                return random.nextInt(songFiles.size());
            } else {
                return (currentIndex + 1) % songFiles.size();
            }
        }
        return -1;
    }

    public int getPreviousSongIndex(int currentIndex) {
        if (currentIndex >= 0) {
            if (shuffleEnabled) {
                return random.nextInt(songFiles.size());
            } else {
                return (currentIndex - 1 + songFiles.size()) % songFiles.size();
            }
        }
        return -1;
    }

    public File getSongFile(int index) {
        if (index >= 0 && index < songFiles.size()) {
            return songFiles.get(index);
        }
        return null;
    }

    public List<File> getSongFiles() {
        return songFiles;
    }

    public boolean isDirectoryLoaded(File directory) {
        return loadedDirectories.contains(directory);
    }

    public void addLoadedDirectory(File directory) {
        if (!loadedDirectories.contains(directory)) {
            loadedDirectories.add(directory);
        }
    }
}

