package com.example.jambo.commands;

import com.example.jambo.managers.MusicPlayerManager;

public class LoopCommand implements Command {
    private final MusicPlayerManager playerManager;

    public LoopCommand(MusicPlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void execute() {
        playerManager.toggleLoop();
    }

    @Override
    public void undo() {
        playerManager.toggleLoop();
    }
}
