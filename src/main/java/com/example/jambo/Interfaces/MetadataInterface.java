package com.example.jambo.Interfaces;

import javafx.scene.image.Image;
import java.io.File;

public interface MetadataInterface {
    String formatSongMetadata(File file) throws Exception;
    AudioMetadata getFileMetadata(File file) throws Exception;
    Image getAlbumArt(File file) throws Exception;

    record AudioMetadata(String format, String bitRate, String sampleRate,
                         String artist, String album, String title) {
    }
}