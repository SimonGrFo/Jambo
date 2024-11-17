package com.example.jambo.managers;

import com.example.jambo.services.MusicPlayerService;
import com.example.jambo.ui.UIUpdater;
import com.example.jambo.event.MediaEventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.util.Duration;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MusicPlayerManager {
    private static final Logger logger = LoggerFactory.getLogger(MusicPlayerManager.class);
    private final MusicPlayerService musicPlayerService;

    private final Label currentSongLabel;
    private final Label timerLabel;
    private final Slider progressSlider;

    private boolean isPlaying = false;
    private boolean isLooping = false;
    private boolean isMuted = false;
    private double currentPosition = 0;
    private double volume = 0.5;

    public MusicPlayerManager(
            MusicPlayerService musicPlayerService,
            PlayerStateManager stateManager,
            MediaEventHandler eventHandler,
            UIUpdater uiUpdater,
            Label currentSongLabel,
            Label timerLabel,
            Slider progressSlider) {
        this.musicPlayerService = musicPlayerService;
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
        isLooping = !isLooping;
        logger.info("Toggled loop state. Is looping: {}", isLooping);
    }

    public void toggleMute() {
        isMuted = !isMuted;
        musicPlayerService.toggleMute();
        logger.info("Toggled mute state. Is muted: {}", isMuted);
    }

    public void seekTo(Duration time) {
        musicPlayerService.seekTo(time.toSeconds());
        currentPosition = time.toSeconds();
        logger.info("Seeked to position: {}", time);
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
        String currentTime = formatTime(current.toSeconds());
        String totalTime = formatTime(total.toSeconds());
        timerLabel.setText(currentTime + " / " + totalTime);
    }

    private String formatTime(double seconds) {
        int minutes = (int) (seconds / 60);
        int remainingSeconds = (int) (seconds % 60);
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    public void setVolume(double newVolume) {
        this.volume = newVolume;
        if (musicPlayerService.getMediaPlayer() != null) {
            musicPlayerService.getMediaPlayer().setVolume(newVolume);
            logger.info("Volume set to: {}", newVolume);
        }
    }

    public boolean isPlaying() { return isPlaying; }
    public boolean isLooping() { return isLooping; }
    public boolean isMuted() { return isMuted; }
    public double getCurrentPosition() { return currentPosition; }
    public double getVolume() { return volume; }
    public int getCurrentSongIndex() {
        return -1; }
    public Duration getTotalDuration() { return musicPlayerService.getTotalDuration(); }

    public void setOnEndOfMedia(Runnable callback) {
        if (musicPlayerService != null) {
            musicPlayerService.setOnEndOfMedia(callback);
        }
    }
}