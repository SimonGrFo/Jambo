package com.example.jambo.managers;

import com.example.jambo.Interfaces.MetadataInterface;
import com.example.jambo.services.MetadataService;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MetadataManager {
    private static final Logger logger = LoggerFactory.getLogger(MetadataManager.class);
    private final MetadataService metadataService;
    private final Label fileInfoLabel;
    private final Label currentSongLabel;
    private final Pane albumArtPane;
    private final ImageView albumArtView;

    public MetadataManager(
            MetadataService metadataService,
            Label fileInfoLabel,
            Label currentSongLabel,
            Pane albumArtPane) {
        this.metadataService = metadataService;
        this.fileInfoLabel = fileInfoLabel;
        this.currentSongLabel = currentSongLabel;
        this.albumArtPane = albumArtPane;

        this.albumArtView = new ImageView();
        this.albumArtView.setFitWidth(100);
        this.albumArtView.setFitHeight(100);
        this.albumArtView.setPreserveRatio(true);
        this.albumArtPane.getChildren().add(albumArtView);
    }

    public String formatSongMetadata(File file) throws Exception {
        return metadataService.formatSongMetadata(file);
    }

    public void updateFileInfo(File songFile) {
        try {
            MetadataInterface.AudioMetadata metadata = metadataService.getFileMetadata(songFile);
            fileInfoLabel.setText(String.format("%s, %s kbps, %s Hz",
                    metadata.format(), metadata.bitRate(), metadata.sampleRate()));
            currentSongLabel.setText(String.format("Playing: %s - %s - %s",
                    metadata.artist(), metadata.album(), metadata.title()));

            updateAlbumArt(songFile);
        } catch (Exception e) {
            fileInfoLabel.setText("Error retrieving metadata");
            logger.error("Error reading metadata for file {}: {}", songFile.getName(), e.getMessage(), e);
            clearAlbumArt();
        }
    }

    private void updateAlbumArt(File songFile) {
        try {
            Image albumArt = metadataService.getAlbumArt(songFile);
            if (albumArt != null) {
                albumArtView.setImage(albumArt);
                albumArtPane.setStyle("-fx-background-color: transparent;");
            } else {
                clearAlbumArt();
            }
        } catch (Exception e) {
            logger.error("Error loading album art for file {}: {}", songFile.getName(), e.getMessage(), e);
            clearAlbumArt();
        }
    }

    private void clearAlbumArt() {
        albumArtView.setImage(null);
        albumArtPane.setStyle("-fx-background-color: #D3D3D3; -fx-border-color: #808080;");
    }
}