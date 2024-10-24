package com.example.jambo.controllers;

import javafx.scene.control.ListView;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class PlaylistController {
    private final List<File> songFiles;
    private final ListView<String> songListView;
    private final List<File> loadedDirectories;
    private boolean shuffleEnabled = false;
    private final Random random = new Random();

    public PlaylistController(ListView<String> songListView) {
        this.songListView = songListView;
        this.songFiles = new ArrayList<>();
        this.loadedDirectories = new ArrayList<>();
    }

    public void addSong(File songFile, String formattedInfo) {
        if (!songFiles.contains(songFile)) {
            songFiles.add(songFile);
            songListView.getItems().add(formattedInfo);
        }
    }

    public void clearPlaylist() {
        songFiles.clear();
        songListView.getItems().clear();
    }

    public void toggleShuffle() {
        shuffleEnabled = !shuffleEnabled;
    }

    public int getNextSongIndex() {
        int currentIndex = songListView.getSelectionModel().getSelectedIndex();
        if (currentIndex >= 0) {
            if (shuffleEnabled) {
                return random.nextInt(songFiles.size());
            } else {
                return (currentIndex + 1) % songFiles.size();
            }
        }
        return -1;
    }

    public int getPreviousSongIndex() {
        int currentIndex = songListView.getSelectionModel().getSelectedIndex();
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