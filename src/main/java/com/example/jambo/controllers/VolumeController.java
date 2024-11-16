package com.example.jambo.controllers;

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

    public interface VolumeChangeListener {
        void onVolumeChanged(double volume, boolean isMuted);
    }

    public void bindToMediaPlayer(MediaPlayer mediaPlayer) {
        this.boundMediaPlayer = mediaPlayer;
        if (mediaPlayer != null) {
            // Set initial volume state
            mediaPlayer.setVolume(isMuted ? 0.0 : volume);

            // Add this controller as a listener to handle volume changes
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

    public void removeVolumeChangeListener(VolumeChangeListener listener) {
        listeners.remove(listener);
    }

    public void setVolume(double newVolume) {
        volume = Math.min(1.0, Math.max(0.0, newVolume));
        notifyListeners();
    }

    public double getVolume() {
        return volume;
    }

    public void toggleMute() {
        isMuted = !isMuted;
        notifyListeners();
    }

    public boolean isMuted() {
        return isMuted;
    }

    private void notifyListeners() {
        for (VolumeChangeListener listener : listeners) {
            listener.onVolumeChanged(volume, isMuted);
        }
    }
}
