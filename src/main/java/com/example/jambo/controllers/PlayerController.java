package com.example.jambo.controllers;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.util.Duration;

public class PlayerController {
    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;
    private boolean isLooping = false;
    private boolean isMuted = false;
    private final Label currentSongLabel;
    private final Label timerLabel;
    private final Slider progressSlider;
    private final Slider volumeSlider;

    public PlayerController(Label currentSongLabel, Label timerLabel,
                            Slider progressSlider, Slider volumeSlider) {
        this.currentSongLabel = currentSongLabel;
        this.timerLabel = timerLabel;
        this.progressSlider = progressSlider;
        this.volumeSlider = volumeSlider;

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null && !isMuted) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });
    }

    public void playMedia(Media media) {
        double currentVolume = volumeSlider.getValue();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnError(() ->
                System.err.println("Media player error: " + mediaPlayer.getError().getMessage()));

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!progressSlider.isPressed()) {
                Duration current = mediaPlayer.getCurrentTime();
                Duration total = mediaPlayer.getTotalDuration();

                if (total != null) {
                    double progress = current.toSeconds() / total.toSeconds();
                    progressSlider.setValue(progress);

                    String currentTime = formatTime(current.toSeconds());
                    String totalTime = formatTime(total.toSeconds());
                    timerLabel.setText(currentTime + " / " + totalTime);
                }
            }
        });

        mediaPlayer.setVolume(isMuted ? 0 : currentVolume);
        mediaPlayer.setCycleCount(isLooping ? MediaPlayer.INDEFINITE : 1);
        mediaPlayer.play();
    }

    private String formatTime(double seconds) {
        int minutes = (int) (seconds / 60);
        int remainingSeconds = (int) (seconds % 60);
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    public void pauseMusic() {
        if (mediaPlayer != null) {
            if (isPaused) {
                mediaPlayer.play();
                isPaused = false;
            } else {
                mediaPlayer.pause();
                isPaused = true;
            }
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            currentSongLabel.setText("No song playing");
            progressSlider.setValue(0);
            timerLabel.setText("0:00 / 0:00");
        }
    }

    public void toggleLoop() {
        isLooping = !isLooping;
        if (mediaPlayer != null) {
            mediaPlayer.setCycleCount(isLooping ? MediaPlayer.INDEFINITE : 1);
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(isMuted ? 0 : volumeSlider.getValue());
        }
    }

    public void seekTo(double time) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(javafx.util.Duration.seconds(time));
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}