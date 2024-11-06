package com.example.jambo.managers;

import com.example.jambo.Interface.IMusicPlayerManager;
import com.example.jambo.Interface.IMusicPlayerService;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.util.Duration;

public class MusicPlayerManager implements IMusicPlayerManager {
    private final IMusicPlayerService musicPlayerService;
    private final Label currentSongLabel;
    private final Label timerLabel;
    private final Slider progressSlider;

    public MusicPlayerManager(IMusicPlayerService musicPlayerService, Label currentSongLabel,
                              Label timerLabel, Slider progressSlider, Slider volumeSlider) {
        this.musicPlayerService = musicPlayerService;
        this.currentSongLabel = currentSongLabel;
        this.timerLabel = timerLabel;
        this.progressSlider = progressSlider;
    }

    @Override
    public void playMedia(javafx.scene.media.Media media) {
        musicPlayerService.playMedia(media);
        setupTimeUpdates();
    }

    private void setupTimeUpdates() {
        musicPlayerService.getMediaPlayer().currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!progressSlider.isPressed()) {
                Duration current = musicPlayerService.getMediaPlayer().getCurrentTime();
                Duration total = musicPlayerService.getMediaPlayer().getTotalDuration();

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

    @Override
    public void pauseMusic() {
        musicPlayerService.pauseMusic();
    }

    @Override
    public void stopMusic() {
        musicPlayerService.stopMusic();
        currentSongLabel.setText("No song playing");
        progressSlider.setValue(0);
        timerLabel.setText("0:00 / 0:00");
    }

    @Override
    public void toggleLoop() {
        musicPlayerService.toggleLoop();
    }

    @Override
    public void toggleMute() {
        musicPlayerService.toggleMute();
    }

    @Override
    public void seekTo(double time) {
        musicPlayerService.seekTo(time);
    }

    @Override
    public double getTotalDuration() {
        return musicPlayerService.getMediaPlayer() != null ?
                musicPlayerService.getMediaPlayer().getTotalDuration().toSeconds() : 0;
    }
}

