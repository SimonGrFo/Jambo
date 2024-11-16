package com.example.jambo.ui;

import com.example.jambo.model.PlayerState;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class UIUpdater {

    private final Label currentSongLabel;
    private final Label timerLabel;
    private final Slider progressSlider;
    private final Slider volumeSlider;

    public UIUpdater(@Qualifier("currentSongLabel") Label currentSongLabel,
                     @Qualifier("timerLabel") Label timerLabel,
                     @Qualifier("progressSlider") Slider progressSlider,
                     @Qualifier("volumeSlider") Slider volumeSlider) {
        this.currentSongLabel = currentSongLabel;
        this.timerLabel = timerLabel;
        this.progressSlider = progressSlider;
        this.volumeSlider = volumeSlider;
    }

    public void updateUI(PlayerState state) {
        volumeSlider.setValue(state.getVolume());

        if (state.getCurrentPosition() > 0) {
            progressSlider.setValue(state.getCurrentPosition());
        }

        if (state.getCurrentSongTitle() != null) {
            currentSongLabel.setText(state.getCurrentSongTitle());
            updateTimerLabel(state.getCurrentPosition(), state.getTotalDuration());
        } else {
            currentSongLabel.setText("No song playing");
            timerLabel.setText("0:00 / 0:00");
        }
    }

    private void updateTimerLabel(double current, double total) {
        String currentTime = formatTime(current);
        String totalTime = formatTime(total);
        timerLabel.setText(currentTime + " / " + totalTime);
    }

    private String formatTime(double seconds) {
        int minutes = (int) (seconds / 60);
        int remainingSeconds = (int) (seconds % 60);
        return String.format("%d:%02d", minutes, remainingSeconds);
    }
}
