package com.example.jambo.commands;

import com.example.jambo.managers.MusicPlayerManager;
import javafx.scene.media.Media;
import javafx.util.Duration;

public class StopCommand implements Command {
    private final MusicPlayerManager musicPlayerManager;
    private final double previousPosition;
    private final boolean wasPlaying;
    private final Media currentMedia;

    public StopCommand(MusicPlayerManager musicPlayerManager, Media currentMedia) {
        this.musicPlayerManager = musicPlayerManager;
        this.previousPosition = musicPlayerManager.getCurrentPosition();
        this.wasPlaying = musicPlayerManager.isPlaying();
        this.currentMedia = currentMedia;
    }

    @Override
    public void execute() {
        musicPlayerManager.stopMusic();
    }

    @Override
    public void undo() {
        if (wasPlaying && currentMedia != null) {
            musicPlayerManager.playMedia(currentMedia);
            musicPlayerManager.seekTo(Duration.seconds(previousPosition));
        }
    }

}
