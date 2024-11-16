package com.example.jambo.commands;

import com.example.jambo.managers.MusicPlayerManager;

public class SeekCommand implements Command {
    private final MusicPlayerManager musicPlayerManager;
    private final double newPosition;
    private final double previousPosition;

    public SeekCommand(MusicPlayerManager musicPlayerManager, double newPosition) {
        this.musicPlayerManager = musicPlayerManager;
        this.newPosition = newPosition;
        this.previousPosition = musicPlayerManager.getCurrentPosition();
    }

    @Override
    public void execute() {
        musicPlayerManager.seekTo(new javafx.util.Duration(newPosition * 1000)); // Convert seconds to milliseconds
    }

    @Override
    public void undo() {
        musicPlayerManager.seekTo(new javafx.util.Duration(previousPosition * 1000)); // Convert seconds to milliseconds
    }
}