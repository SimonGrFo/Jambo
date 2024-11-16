package com.example.jambo.controllers;

import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class VolumeController {
    private final Slider volumeSlider;
    private boolean isMuted;

    public VolumeController(@Qualifier("volumeSlider") Slider volumeSlider) {
        this.volumeSlider = volumeSlider;
    }

    public void bindVolumeControl(MediaPlayer player) {
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) ->
                updateVolume(player, newValue.doubleValue()));
    }

    private void updateVolume(MediaPlayer player, double volume) {
        if (player != null && !isMuted) {
            player.setVolume(volume);
        }
    }

    public void toggleMute(MediaPlayer player) {
        isMuted = !isMuted;
        player.setVolume(isMuted ? 0 : volumeSlider.getValue());
    }
}