package com.example.jambo.services;

import com.example.jambo.Interfaces.MusicPlayerInterface;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.jambo.util.LoggerUtil;

public class MusicPlayerService implements MusicPlayerInterface {
    private static final Logger logger = LoggerFactory.getLogger(MusicPlayerService.class);
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

    private void setupMediaPlayer(MediaPlayer player, double volume) {
        player.setOnError(() -> {
            MediaException error = player.getError();
            logger.error("MediaPlayer error: {}", error.getMessage());
        });

        player.setVolume(isMuted ? 0 : volume);
        player.setCycleCount(isLooping ? MediaPlayer.INDEFINITE : 1);
        setupEndOfMediaHandler(player);
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
        try {
            if (mediaPlayer != null) {
                MediaPlayer oldPlayer = mediaPlayer;
                javafx.application.Platform.runLater(() -> {
                    oldPlayer.stop();
                    oldPlayer.dispose();
                });
            }

            mediaPlayer = new MediaPlayer(media);
            setupMediaPlayer(mediaPlayer, volumeSlider.getValue());

            mediaPlayer.setOnReady(() -> {
                mediaPlayer.play();
            });
        } catch (Exception e) {
            logger.error("Failed to play media: {}", e.getMessage());
            throw e;
        }
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