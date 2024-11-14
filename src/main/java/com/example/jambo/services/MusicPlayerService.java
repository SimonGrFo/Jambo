package com.example.jambo.services;

import com.example.jambo.Interfaces.MusicPlayerInterface;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MusicPlayerService implements MusicPlayerInterface {
    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;
    private boolean isLooping = false;
    private boolean isMuted = false;
    private final Slider volumeSlider;
    private Runnable onEndOfMedia;

    public MusicPlayerService(Slider volumeSlider) {
        this.volumeSlider = volumeSlider;
        setupVolumeControl();
    }

    public MusicPlayerService(Slider volumeSlider, MediaPlayer mediaPlayer) {
        this.volumeSlider = volumeSlider;
        this.mediaPlayer = mediaPlayer;
        setupVolumeControl();
    }

    public void setOnEndOfMedia(Runnable callback) {
        this.onEndOfMedia = callback;
        if (mediaPlayer != null) {
            setupEndOfMediaHandler(mediaPlayer);
        }
    }

    private void setupVolumeControl() {
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null && !isMuted) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });
    }

    private void setupNewPlayer(Media media, double currentVolume) {
        if (mediaPlayer != null) {
            MediaPlayer oldPlayer = mediaPlayer;
            javafx.application.Platform.runLater(() -> {
                oldPlayer.stop();
                oldPlayer.dispose();
            });
        }

        mediaPlayer = new MediaPlayer(media);
        setupMediaPlayer(mediaPlayer, currentVolume);

        mediaPlayer.setOnReady(() -> mediaPlayer.play());
    }

    private void setupMediaPlayer(MediaPlayer player, double volume) {
        player.setOnError(() ->
                System.err.println("Media player error: " + player.getError().getMessage())
        );

        player.setVolume(isMuted ? 0 : volume);
        player.setCycleCount(isLooping ? MediaPlayer.INDEFINITE : 1);
        setupEndOfMediaHandler(player);

        player.statusProperty().addListener((observable, oldValue, newValue) -> System.out.println("MediaPlayer status changed from " + oldValue + " to " + newValue));
    }

    private void setupEndOfMediaHandler(MediaPlayer player) {
        player.setOnEndOfMedia(() -> {
            if (!isLooping && onEndOfMedia != null) {
                player.seek(Duration.ZERO);
                player.stop();
                onEndOfMedia.run();
            }
        });
    }

    @Override
    public void playMedia(Media media) {
        if (mediaPlayer == null || !mediaPlayer.getMedia().equals(media)) {
            setupNewPlayer(media, volumeSlider.getValue());
        }
        mediaPlayer.play();
    }

    @Override
    public void pauseMedia() {
        if (mediaPlayer != null) {
            if (isPaused) {
                mediaPlayer.play();
            } else {
                mediaPlayer.pause();
            }
            isPaused = !isPaused;
        }
    }

    @Override
    public void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPaused = false;
        }
    }

    @Override
    public void toggleLoop() {
        isLooping = !isLooping;
        if (mediaPlayer != null) {
            mediaPlayer.setCycleCount(isLooping ? MediaPlayer.INDEFINITE : 1);
        }
    }

    @Override
    public void toggleMute() {
        isMuted = !isMuted;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(isMuted ? 0 : volumeSlider.getValue());
        }
    }

    @Override
    public void seekTo(double time) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(time));
        }
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public Duration getTotalDuration() {
        return mediaPlayer != null ? mediaPlayer.getTotalDuration() : Duration.ZERO;
    }
}