package com.example.jambo.services;

import com.example.jambo.Interfaces.MusicPlayerInterface;
import com.example.jambo.controllers.VolumeController;
import com.example.jambo.managers.PlayerStateManager;
import com.example.jambo.event.MediaEventHandler;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.springframework.stereotype.Service;

@Service
public class MusicPlayerService implements MusicPlayerInterface {
    private MediaPlayer mediaPlayer;
    private Media currentMedia;
    private final VolumeController volumeController;
    private final PlayerStateManager stateManager;
    private final MediaEventHandler eventHandler;

    private boolean isPaused = false;
    private boolean isLooping = false;
    private boolean isMuted = false;

    public MusicPlayerService(VolumeController volumeController,
                              PlayerStateManager stateManager,
                              MediaEventHandler eventHandler) {
        this.volumeController = volumeController;
        this.stateManager = stateManager;
        this.eventHandler = eventHandler;
    }

    @Override
    public void playMedia(Media media) {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }

        currentMedia = media;
        mediaPlayer = new MediaPlayer(media);

        // Initialize player state
        stateManager.applyCurrentState(mediaPlayer);
        volumeController.bindVolumeControl(mediaPlayer);
        eventHandler.initializeEventHandlers(mediaPlayer);

        mediaPlayer.play();
        isPaused = false;
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
            mediaPlayer.dispose();
            mediaPlayer = null;
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
            volumeController.toggleMute(mediaPlayer);
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

    public void setOnEndOfMedia(Runnable callback) {
        if (mediaPlayer != null) {
            eventHandler.setOnEndOfMedia(callback);
        }
    }

    public Media getCurrentMedia() {
        return currentMedia;
    }
}