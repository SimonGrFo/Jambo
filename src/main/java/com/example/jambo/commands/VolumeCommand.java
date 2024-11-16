package com.example.jambo.commands;

import com.example.jambo.managers.MusicPlayerManager;

public class VolumeCommand implements Command {
    private final MusicPlayerManager musicPlayerManager;
    private final double newVolume;
    private final double previousVolume;

    public VolumeCommand(MusicPlayerManager musicPlayerManager, double newVolume) {
        this.musicPlayerManager = musicPlayerManager;
        this.newVolume = newVolume;
        this.previousVolume = musicPlayerManager.getVolume();
    }

    @Override
    public void execute() {
        musicPlayerManager.setVolume(newVolume);
    }

    @Override
    public void undo() {
        musicPlayerManager.setVolume(previousVolume);
    }
}