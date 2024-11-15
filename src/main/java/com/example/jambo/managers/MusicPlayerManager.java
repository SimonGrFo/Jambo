package com.example.jambo.managers;

import com.example.jambo.Interfaces.MusicPlayerInterface;
import com.example.jambo.services.MusicPlayerService;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MusicPlayerManager {
    private final MusicPlayerService musicPlayerService;
    private final Label currentSongLabel;
    private final Label timerLabel;
    private final Slider progressSlider;

    public MusicPlayerManager(
            MusicPlayerService musicPlayerService,
            Label currentSongLabel,
            Label timerLabel,
            Slider progressSlider) {
        this.musicPlayerService = musicPlayerService;
        this.currentSongLabel = currentSongLabel;
        this.timerLabel = timerLabel;
        this.progressSlider = progressSlider;
    }

    public void playMedia(Media media) {
        musicPlayerService.playMedia(media);
        setupTimeUpdates();
    }

    private void setupTimeUpdates() {
        musicPlayerService.getMediaPlayer().currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!progressSlider.isPressed()) {
                Duration current = musicPlayerService.getMediaPlayer().getCurrentTime();
                Duration total = musicPlayerService.getTotalDuration();

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
        musicPlayerService.pauseMedia();
    }

    public void stopMusic() {
        musicPlayerService.stopMedia();
        currentSongLabel.setText("No song playing");
        progressSlider.setValue(0);
        timerLabel.setText("0:00 / 0:00");
    }

    public void toggleLoop() {
        musicPlayerService.toggleLoop();
    }

    public void toggleMute() {
        musicPlayerService.toggleMute();
    }

    public void seekTo(double time) {
        musicPlayerService.seekTo(time);
    }

    public double getTotalDuration() {
        return musicPlayerService.getMediaPlayer() != null ?
                musicPlayerService.getTotalDuration().toSeconds() : 0;
    }

    public void setOnEndOfMedia(Runnable callback) {
        if (musicPlayerService != null) {
            ((MusicPlayerService) musicPlayerService).setOnEndOfMedia(callback);
        }
    }
}