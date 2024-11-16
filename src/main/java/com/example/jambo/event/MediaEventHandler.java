package com.example.jambo.event;

import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MediaEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(MediaEventHandler.class);
    private Runnable onEndOfMedia;

    public void initializeEventHandlers(MediaPlayer player) {
        setupErrorHandler(player);
        setupEndOfMediaHandler(player);
        setupTimeUpdateHandler(player);
    }

    private void setupErrorHandler(MediaPlayer player) {
        player.setOnError(() -> {
            MediaException error = player.getError();
            logger.error("MediaPlayer error: {}", error.getMessage());
        });
    }

    private void setupTimeUpdateHandler(MediaPlayer player) {
        player.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
        });
    }

    private void setupEndOfMediaHandler(MediaPlayer player) {
        player.setOnEndOfMedia(() -> {
            if (onEndOfMedia != null) {
                player.seek(Duration.ZERO);
                player.stop();
                onEndOfMedia.run();
            }
        });
    }

    public void setOnEndOfMedia(Runnable callback) {
        this.onEndOfMedia = callback;
    }
}