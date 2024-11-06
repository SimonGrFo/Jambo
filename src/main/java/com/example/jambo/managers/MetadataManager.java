package com.example.jambo.managers;

import com.example.jambo.Interfaces.MetadataInterface;
import javafx.scene.control.Label;
import java.io.File;

public class MetadataManager {
    private final MetadataInterface metadataService;
    private final Label fileInfoLabel;
    private final Label currentSongLabel;

    public MetadataManager(MetadataInterface metadataService, Label fileInfoLabel,
                           Label currentSongLabel) {
        this.metadataService = metadataService;
        this.fileInfoLabel = fileInfoLabel;
        this.currentSongLabel = currentSongLabel;
    }

    public String formatSongMetadata(File file) throws Exception {
        return metadataService.formatSongMetadata(file);
    }

    public void updateFileInfo(File songFile) {
        try {
            MetadataInterface.AudioMetadata metadata = metadataService.getFileMetadata(songFile);
            fileInfoLabel.setText(String.format("%s, %s kbps, %s Hz",
                    metadata.format, metadata.bitRate, metadata.sampleRate));
            currentSongLabel.setText(String.format("Playing: %s - %s - %s",
                    metadata.artist, metadata.album, metadata.title));
        } catch (Exception e) {
            fileInfoLabel.setText("Error retrieving metadata");
            System.err.println("Error reading metadata: " + e.getMessage());
        }
    }
}
