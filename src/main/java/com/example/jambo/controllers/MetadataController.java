package com.example.jambo.controllers;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import java.io.ByteArrayInputStream;
import java.io.File;

public class MetadataController {
    private final Label fileInfoLabel;
    private final Label currentSongLabel;
    private final ImageView albumArtView;

    public MetadataController(Label fileInfoLabel, Label currentSongLabel, ImageView albumArtView) {
        this.fileInfoLabel = fileInfoLabel;
        this.currentSongLabel = currentSongLabel;
        this.albumArtView = albumArtView;
    }

    public String formatSongMetadata(File file) throws Exception {
        AudioFile audioFile = AudioFileIO.read(file);
        AudioHeader audioHeader = audioFile.getAudioHeader();
        Tag tag = audioFile.getTag();

        String artist = tag.getFirst(org.jaudiotagger.tag.FieldKey.ARTIST);
        String album = tag.getFirst(org.jaudiotagger.tag.FieldKey.ALBUM);
        String title = tag.getFirst(org.jaudiotagger.tag.FieldKey.TITLE);
        int durationInSeconds = audioHeader.getTrackLength();
        String duration = formatTime(durationInSeconds);

        if (artist == null || artist.isEmpty()) artist = "Unknown Artist";
        if (album == null || album.isEmpty()) album = "Unknown Album";
        if (title == null || title.isEmpty()) title = file.getName();

        return String.format("%s - %s - %s (%s)", artist, album, title, duration);
    }

    public void updateFileInfo(File songFile) {
        try {
            AudioFile audioFile = AudioFileIO.read(songFile);
            AudioHeader audioHeader = audioFile.getAudioHeader();
            Tag tag = audioFile.getTag();

            String format = audioHeader.getFormat();
            String bitrate = audioHeader.getBitRate();
            String sampleRate = audioHeader.getSampleRate();

            String artist = tag.getFirst(org.jaudiotagger.tag.FieldKey.ARTIST);
            String album = tag.getFirst(org.jaudiotagger.tag.FieldKey.ALBUM);
            String title = tag.getFirst(org.jaudiotagger.tag.FieldKey.TITLE);

            fileInfoLabel.setText(String.format("%s, %s kbps, %s Hz", format, bitrate, sampleRate));
            currentSongLabel.setText(String.format("Playing: %s - %s - %s", artist, album, title));

            updateAlbumArt(tag);
        } catch (Exception e) {
            fileInfoLabel.setText("Error retrieving metadata");
            System.err.println("Error reading metadata: " + e.getMessage());
            loadDefaultAlbumArt();
        }
    }

    private void updateAlbumArt(Tag tag) {
        try {
            Artwork artwork = tag.getFirstArtwork();
            if (artwork != null && artwork.getBinaryData() != null) {
                Image albumArtImage = new Image(new ByteArrayInputStream(artwork.getBinaryData()));
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
            Image defaultImage = new Image(getClass().getResourceAsStream("/default_album_art.png"));
            albumArtView.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Error loading default album art: " + e.getMessage());
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }
}
