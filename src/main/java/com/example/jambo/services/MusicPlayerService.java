package com.example.jambo.services;

import com.example.jambo.Interface.IMusicPlayerService;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MusicPlayerService implements IMusicPlayerService {
    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;
    private boolean isLooping = false;
    private boolean isMuted = false;
    private final Slider volumeSlider;

    public MusicPlayerService(Slider volumeSlider) {
        this.volumeSlider = volumeSlider;
        setupVolumeControl();
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

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnError(() ->
                System.err.println("Media player error: " + mediaPlayer.getError().getMessage()));

        mediaPlayer.setVolume(isMuted ? 0 : currentVolume);
        mediaPlayer.setCycleCount(isLooping ? MediaPlayer.INDEFINITE : 1);
        mediaPlayer.play();
    }

    @Override
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

    @Override
    public void stopMusic() {
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
}
