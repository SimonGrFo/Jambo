package com.example.jambo.commands;

import com.example.jambo.exceptions.CommandExecutionException;
import com.example.jambo.services.MusicPlayerService;
import javafx.scene.media.Media;
import java.io.File;

public class PlayCommand implements Command {
    private final MusicPlayerService playerService;
    private final File songFile;
    private Media previousMedia;

    public PlayCommand(MusicPlayerService playerService, File songFile) {
        this.playerService = playerService;
        this.songFile = songFile;
    }

    @Override
    public void execute() {
        try {
            Media media = new Media(songFile.toURI().toString());
            previousMedia = playerService.getCurrentMedia();
            playerService.playMedia(media);
        } catch (Exception e) {
            throw new CommandExecutionException("Failed to execute play command", e);
        }
    }

    @Override
    public void undo() {
        if (previousMedia != null) {
            playerService.playMedia(previousMedia);
        } else {
            playerService.stopMedia();
        }
    }

}
