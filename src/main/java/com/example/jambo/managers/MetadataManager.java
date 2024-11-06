package com.example.jambo.managers;

import com.example.jambo.Interface.IMetadataManager;
import com.example.jambo.Interface.IMetadataService;
import com.example.jambo.services.MetadataService;
import javafx.scene.control.Label;
import java.io.File;

public class MetadataManager implements IMetadataManager {
    private final IMetadataService metadataService;
    private final Label fileInfoLabel;
    private final Label currentSongLabel;

    public MetadataManager(IMetadataService metadataService, Label fileInfoLabel,
                           Label currentSongLabel) {
        this.metadataService = metadataService;
        this.fileInfoLabel = fileInfoLabel;
        this.currentSongLabel = currentSongLabel;
    }

    @Override
    public String formatSongMetadata(File file) throws Exception {
        return metadataService.formatSongMetadata(file);
    }

    @Override
    public void updateFileInfo(File songFile) {
        try {
            IMetadataService.AudioMetadata metadata = metadataService.getFileMetadata(songFile);

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

