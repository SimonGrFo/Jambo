package com.example.jambo.managers;

import com.example.jambo.services.MetadataService;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Objects;

public class MetadataManager {
    private final MetadataService metadataService;
    private final Label fileInfoLabel;
    private final Label currentSongLabel;
    private final ImageView albumArtView;

    public MetadataManager(Label fileInfoLabel, Label currentSongLabel, ImageView albumArtView) {
        this.fileInfoLabel = fileInfoLabel;
        this.currentSongLabel = currentSongLabel;
        this.albumArtView = albumArtView;
        this.metadataService = new MetadataService();
    }

    public String formatSongMetadata(File file) throws Exception {
        return metadataService.formatSongMetadata(file);
    }

    public void updateFileInfo(File songFile) {
        try {
            MetadataService.AudioMetadata metadata = metadataService.getFileMetadata(songFile);

            fileInfoLabel.setText(String.format("%s, %s kbps, %s Hz",
                    metadata.format, metadata.bitRate, metadata.sampleRate));
            currentSongLabel.setText(String.format("Playing: %s - %s - %s",
                    metadata.artist, metadata.album, metadata.title));

            updateAlbumArt(metadata);
        } catch (Exception e) {
            fileInfoLabel.setText("Error retrieving metadata");
            System.err.println("Error reading metadata: " + e.getMessage());
            loadDefaultAlbumArt();
        }
    }

    private void updateAlbumArt(MetadataService.AudioMetadata metadata) {
        try {
            if (metadata.artwork != null && metadata.artwork.getBinaryData() != null) {
                Image albumArtImage = new Image(
                        new ByteArrayInputStream(metadata.artwork.getBinaryData())
                );
                albumArtView.setImage(albumArtImage);
            } else {
                loadDefaultAlbumArt();
            }
        } catch (Exception e) {
            loadDefaultAlbumArt();
        }
    }

    private void loadDefaultAlbumArt() {
        try {
            Image defaultImage = new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/images/default_album_art.png"))
                    //TODO - somewhat broken
            );
            albumArtView.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Error loading default album art: " + e.getMessage());
        }
    }
}
