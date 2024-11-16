package com.example.jambo.commands;

import com.example.jambo.managers.MusicPlayerManager;
import javafx.scene.media.Media;
import java.io.File;

public class PlayCommand implements Command {
    private final MusicPlayerManager musicPlayerManager;
    private final Media media;

    public PlayCommand(MusicPlayerManager musicPlayerManager, File songFile) {
        this.musicPlayerManager = musicPlayerManager;
        this.media = new Media(songFile.toURI().toString());
    }

    @Override
    public void execute() {
        musicPlayerManager.playMedia(media);
    }

    @Override
    public void undo() {
        musicPlayerManager.stopMusic();
    }
}
