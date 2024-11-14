package com.example.jambo.managers;

import com.example.jambo.Interfaces.MusicPlayerInterface;
import com.example.jambo.services.MusicPlayerService;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.util.Duration;

public class MusicPlayerManager {
    private final MusicPlayerInterface musicPlayer;
    private final Label currentSongLabel;
    private final Label timerLabel;
    private final Slider progressSlider;

    public MusicPlayerManager(MusicPlayerInterface musicPlayer, Label currentSongLabel,
                              Label timerLabel, Slider progressSlider) {
        this.musicPlayer = musicPlayer;
        this.currentSongLabel = currentSongLabel;
        this.timerLabel = timerLabel;
        this.progressSlider = progressSlider;
    }

    public void playMedia(Media media) {
        musicPlayer.playMedia(media);
        setupTimeUpdates();
    }

    private void setupTimeUpdates() {
        musicPlayer.getMediaPlayer().currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!progressSlider.isPressed()) {
                Duration current = musicPlayer.getMediaPlayer().getCurrentTime();
                Duration total = musicPlayer.getTotalDuration();

                if (total != null) {
                    double progress = current.toSeconds() / total.toSeconds();
                    progressSlider.setValue(progress);

                    String currentTime = formatTime(current.toSeconds());
                    String totalTime = formatTime(total.toSeconds());
                    timerLabel.setText(currentTime + " / " + totalTime);
                }
            }
        });
    }

    private String formatTime(double seconds) {
        int minutes = (int) (seconds / 60);
        int remainingSeconds = (int) (seconds % 60);
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    public void pauseMusic() {
        musicPlayer.pauseMedia();
    }

    public void stopMusic() {
        musicPlayer.stopMedia();
        currentSongLabel.setText("No song playing");
        progressSlider.setValue(0);
        timerLabel.setText("0:00 / 0:00");
    }

    public void toggleLoop() {
        musicPlayer.toggleLoop();
    }

    public void toggleMute() {
        musicPlayer.toggleMute();
    }

    public void seekTo(double time) {
        musicPlayer.seekTo(time);
    }

    public double getTotalDuration() {
        return musicPlayer.getMediaPlayer() != null ?
                musicPlayer.getTotalDuration().toSeconds() : 0;
    }

    public void setOnEndOfMedia(Runnable callback) {
        if (musicPlayer instanceof MusicPlayerService) {
            ((MusicPlayerService) musicPlayer).setOnEndOfMedia(callback);
        }
    }
}