package com.example.jambo.managers;

import com.example.jambo.controllers.VolumeController;
import com.example.jambo.services.MusicPlayerService;
import com.example.jambo.utils.TimeFormatter;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MusicPlayerManager {
    private static final Logger logger = LoggerFactory.getLogger(MusicPlayerManager.class);
    private final MusicPlayerService musicPlayerService;
    private final VolumeController volumeController;
    private final Label currentSongLabel;
    private final Label timerLabel;
    private final Slider progressSlider;

    private boolean isPlaying = false;
    private boolean isLooping = false;
    private double currentPosition = 0;

    public MusicPlayerManager(
            MusicPlayerService musicPlayerService,
            VolumeController volumeController,
            Label currentSongLabel,
            Label timerLabel,
            Slider progressSlider) {
        this.musicPlayerService = musicPlayerService;
        this.volumeController = volumeController;
        this.currentSongLabel = currentSongLabel;
        this.timerLabel = timerLabel;
        this.progressSlider = progressSlider;
    }

    public void playMedia(Media media) {
        try {
            musicPlayerService.playMedia(media);
            isPlaying = true;
            setupTimeUpdates();
            logger.info("Started playing media");
        } catch (Exception e) {
            logger.error("Failed to play media: {}", e.getMessage());
            throw e;
        }
    }

    public void pauseMedia() {
        musicPlayerService.pauseMedia();
        isPlaying = !isPlaying;
        logger.info("Toggled pause state. Is playing: {}", isPlaying);
    }

    public void stopMusic() {
        musicPlayerService.stopMedia();
        isPlaying = false;
        currentSongLabel.setText("No song playing");
        progressSlider.setValue(0);
        timerLabel.setText("0:00 / 0:00");
        logger.info("Stopped music playback");
    }

    public void toggleLoop() {
        musicPlayerService.toggleLoop();
        logger.info("Toggled loop state. Is looping: {}", musicPlayerService.isLooping());
    }

    public void toggleMute() {
        volumeController.toggleMute();
        logger.info("Toggled mute state. Is muted: {}", volumeController.isMuted());
    }

    public void seekTo(Duration time) {
        musicPlayerService.seekTo(time.toSeconds());
        currentPosition = time.toSeconds();
    }

    private void setupTimeUpdates() {
        if (musicPlayerService.getMediaPlayer() != null) {
            musicPlayerService.getMediaPlayer().currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!progressSlider.isPressed()) {
                    Duration current = musicPlayerService.getMediaPlayer().getCurrentTime();
                    Duration total = getTotalDuration();
                    currentPosition = current.toSeconds();

                    if (total != null) {
                        double progress = current.toSeconds() / total.toSeconds();
                        progressSlider.setValue(progress);
                        updateTimerLabel(current, total);
                    }
                }
            });
        }
    }

    private void updateTimerLabel(Duration current, Duration total) {
        String currentTime = TimeFormatter.formatTime(current.toSeconds());
        String totalTime = TimeFormatter.formatTime(total.toSeconds());
        timerLabel.setText(currentTime + " / " + totalTime);
    }

    public Duration getTotalDuration() { return musicPlayerService.getTotalDuration(); }

    public void setOnEndOfMedia(Runnable callback) {
        if (musicPlayerService != null) {
            musicPlayerService.setOnEndOfMedia(callback);
        }
    }
}
