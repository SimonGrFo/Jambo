package com.example.jambo.services;

import com.example.jambo.Interfaces.MusicPlayerInterface;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.prefs.Preferences;

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

    public void setOnEndOfMedia(Runnable callback) {
        this.onEndOfMedia = callback;
    }

    private void setupVolumeControl() {
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null && !isMuted) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });
    }

    @Override
    public void playMedia(Media media) {
        double currentVolume = volumeSlider.getValue();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        setupNewPlayer(media, currentVolume);
    }

    private void setupNewPlayer(Media media, double currentVolume) {
        mediaPlayer = new MediaPlayer(media);
        setupMediaPlayer(mediaPlayer, currentVolume);
        mediaPlayer.play();
    }

    private void setupMediaPlayer(MediaPlayer player, double volume) {
        player.setOnError(() ->
                System.err.println("Media player error: " + player.getError().getMessage()));

        player.setVolume(isMuted ? 0 : volume);
        player.setCycleCount(isLooping ? MediaPlayer.INDEFINITE : 1);

        player.setOnEndOfMedia(() -> {
            if (!isLooping && onEndOfMedia != null) {
                onEndOfMedia.run();
            }
        });
    }

    @Override
    public void pauseMedia() {
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

    @Override
    public void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
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
