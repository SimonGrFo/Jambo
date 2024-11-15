package com.example.jambo.services;

import com.example.jambo.Interfaces.MetadataInterface;
import javafx.scene.image.Image;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
@Component

public class MetadataService implements MetadataInterface {
    private AudioFile readAudioFile(File file) throws Exception {
        return AudioFileIO.read(file);
    }

    private String getSafeTagValue(Tag tag, org.jaudiotagger.tag.FieldKey key, String defaultValue) {
        String value = tag.getFirst(key);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    @Override
    public String formatSongMetadata(File file) throws Exception {
        AudioFile audioFile = readAudioFile(file);
        AudioHeader audioHeader = audioFile.getAudioHeader();
        Tag tag = audioFile.getTag();

        String artist = getSafeTagValue(tag, org.jaudiotagger.tag.FieldKey.ARTIST, "Unknown Artist");
        String album = getSafeTagValue(tag, org.jaudiotagger.tag.FieldKey.ALBUM, "Unknown Album");
        String title = getSafeTagValue(tag, org.jaudiotagger.tag.FieldKey.TITLE, file.getName());

        int durationInSeconds = audioHeader.getTrackLength();
        String duration = formatTime(durationInSeconds);

        return String.format("%s - %s - %s (%s)", artist, album, title, duration);
    }

    @Override
    public AudioMetadata getFileMetadata(File songFile) throws Exception {
        AudioFile audioFile = readAudioFile(songFile);
        AudioHeader audioHeader = audioFile.getAudioHeader();
        Tag tag = audioFile.getTag();

        return new AudioMetadata(
                audioHeader.getFormat(),
                audioHeader.getBitRate(),
                audioHeader.getSampleRate(),
                getSafeTagValue(tag, org.jaudiotagger.tag.FieldKey.ARTIST, "Unknown Artist"),
                getSafeTagValue(tag, org.jaudiotagger.tag.FieldKey.ALBUM, "Unknown Album"),
                getSafeTagValue(tag, org.jaudiotagger.tag.FieldKey.TITLE, songFile.getName())
        );
    }

    @Override
    public Image getAlbumArt(File file) throws Exception {
        AudioFile audioFile = readAudioFile(file);
        Tag tag = audioFile.getTag();

        if (tag != null) {
            Artwork artwork = tag.getFirstArtwork();
            if (artwork != null) {
                byte[] imageData = artwork.getBinaryData();
                return new Image(new ByteArrayInputStream(imageData));
            }
        }
        return null;
    }
}