package com.example.jambo.player;

import com.example.jambo.model.Track;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicPlayer {
    private List<Track> tracks;
    private AdvancedPlayer player;
    private int currentTrackIndex = 0;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isPaused = false;

    public MusicPlayer(List<Track> tracks) {
        this.tracks = tracks;
    }

    public void updateTracks(List<Track> newTracks) {
        this.tracks = newTracks;
    }

    public void playTrack(int index) {
        stop();

        currentTrackIndex = index;
        executor.submit(() -> {
            try (FileInputStream fis = new FileInputStream(tracks.get(currentTrackIndex).path())) {
                player = new AdvancedPlayer(fis);
                player.play();
            } catch (JavaLayerException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void stop() {
        if (player != null) {
            player.close();
            player = null;
        }
    }

    public void pause() {
        if (player != null) {
            isPaused = true;
            player.close();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
