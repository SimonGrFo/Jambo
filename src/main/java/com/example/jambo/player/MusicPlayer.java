package com.example.jambo.player;

import com.example.jambo.model.Track;
import javafx.concurrent.Task;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicPlayer {
    private List<Track> tracks;
    private AdvancedPlayer player;
    private int currentTrackIndex = 0;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isPaused = false;
    private FileInputStream fileInputStream;
    private long totalDuration = 0;
    private long currentPosition = 0;

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
                totalDuration = getTrackDuration(tracks.get(currentTrackIndex).path());
                player.play();
            } catch (JavaLayerException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public double getCurrentProgress() {
        if (totalDuration == 0) return 0;
        return (double) currentPosition / totalDuration;
    }

    private long getTrackDuration(String path) {
        return 0;  //TODO - this needs to be a thing, which it isnt. :(
    }
    public void stop() {
        if (player != null) {
            player.close();
            player = null;
            isPaused = false;
        }
    }

    public boolean isPlaying() {
        return player != null && !isPaused;
    }

    private void startProgressUpdate() {
        Task<Void> progressTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (player != null && !isPaused) {
                    currentPosition += 500;
                    updateProgress(getCurrentProgress(), 1);
                    Thread.sleep(500);
                }
                return null;
            }
        };

        new Thread(progressTask).start();
    }


    public void pause() {
        if (player != null && !isPaused) {
            player.close();
            isPaused = true;
        }
    }

    public void resume() {
        if (isPaused) {
            executor.submit(() -> {
                try {
                    fileInputStream = new FileInputStream(tracks.get(currentTrackIndex).path());
                    player = new AdvancedPlayer(fileInputStream);
                    player.play();
                    isPaused = false;
                } catch (JavaLayerException | IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void previousTrack() {
        if (currentTrackIndex > 0) {
            playTrack(--currentTrackIndex);
        }
    }

    public void nextTrack() {
        if (currentTrackIndex < tracks.size() - 1) {
            playTrack(++currentTrackIndex);
        }
    }

    public void shuffleTracks() {
        Collections.shuffle(tracks);
        playTrack(0);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
