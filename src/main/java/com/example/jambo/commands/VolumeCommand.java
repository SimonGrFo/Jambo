package com.example.jambo.commands;

import com.example.jambo.controllers.VolumeController;
import com.example.jambo.managers.MusicPlayerManager;

public class VolumeCommand implements Command {
    private final VolumeController volumeController;
    private final double newVolume;
    private final double previousVolume;

    public VolumeCommand(VolumeController volumeController, double newVolume) {
        this.volumeController = volumeController;
        this.newVolume = newVolume;
        this.previousVolume = volumeController.getVolume();
    }

    @Override
    public void execute() {
        volumeController.setVolume(newVolume);
    }

    @Override
    public void undo() {
        volumeController.setVolume(previousVolume);
    }
}