package com.example.jambo.commands;

import com.example.jambo.managers.MusicPlayerManager;

public class PauseCommand implements Command {
    private final MusicPlayerManager musicPlayerManager;
    private final boolean wasPlaying;

    public PauseCommand(MusicPlayerManager musicPlayerManager) {
        this.musicPlayerManager = musicPlayerManager;
        this.wasPlaying = musicPlayerManager.isPlaying();
    }

    @Override
    public void execute() {
        musicPlayerManager.pauseMedia();
    }

    @Override
    public void undo() {
        if (wasPlaying) {
            musicPlayerManager.pauseMedia();
        }
    }

}