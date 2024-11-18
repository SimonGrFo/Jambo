package com.example.jambo.controllers;

import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class VolumeController {
    private double volume = 0.5;
    private boolean isMuted = false;
    private final List<VolumeChangeListener> listeners = new ArrayList<>();
    private MediaPlayer boundMediaPlayer;
    private final Slider volumeSlider;

    public interface VolumeChangeListener {
        void onVolumeChanged(double volume, boolean isMuted);
    }

    public void bindToMediaPlayer(MediaPlayer mediaPlayer) {
        this.boundMediaPlayer = mediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(isMuted ? 0.0 : volume);

            addVolumeChangeListener((newVolume, muted) -> {
                if (boundMediaPlayer != null) {
                    boundMediaPlayer.setVolume(muted ? 0.0 : newVolume);
                }
            });
        }
    }

    public void addVolumeChangeListener(VolumeChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (volumeSlider != null) {
            volumeSlider.setDisable(isMuted);
        }
        notifyListeners();
    }

    public boolean isMuted() {
        return isMuted;
    }

    public VolumeController(Slider volumeSlider) {
        this.volumeSlider = volumeSlider;

        volumeSlider.setValue(volume);

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            volume = newValue.doubleValue();
            notifyListeners();
        });
    }

    public void setVolume(double newVolume) {
        volume = Math.max(0.0, Math.min(1.0, newVolume));
        if (volumeSlider != null) {
            volumeSlider.setValue(volume);
        }
        notifyListeners();
    }

    public double getVolume() {
        return volume;
    }

    private void notifyListeners() {
        for (VolumeChangeListener listener : listeners) {
            listener.onVolumeChanged(volume, isMuted);
        }
    }
}
