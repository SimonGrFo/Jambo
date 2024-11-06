package com.example.jambo.services;

import com.example.jambo.Interfaces.MetadataInterface;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class MetadataService implements MetadataInterface {
    @Override
    public String formatSongMetadata(File file) {
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            AudioHeader audioHeader = audioFile.getAudioHeader();
            Tag tag = audioFile.getTag();

            String artist = tag.getFirst(org.jaudiotagger.tag.FieldKey.ARTIST);
            String album = tag.getFirst(org.jaudiotagger.tag.FieldKey.ALBUM);
            String title = tag.getFirst(org.jaudiotagger.tag.FieldKey.TITLE);
            int durationInSeconds = audioHeader.getTrackLength();
            String duration = formatTime(durationInSeconds);

            artist = (artist == null || artist.isEmpty()) ? "Unknown Artist" : artist;
            album = (album == null || album.isEmpty()) ? "Unknown Album" : album;
            title = (title == null || title.isEmpty()) ? file.getName() : title;

            return String.format("%s - %s - %s (%s)", artist, album, title, duration);
        } catch (Exception e) {
            return "Unknown Artist - Unknown Album - " + file.getName() + " (Unknown Duration)";
        }
    }

    @Override
    public AudioMetadata getFileMetadata(File songFile) {
        try {
            AudioFile audioFile = AudioFileIO.read(songFile);
            AudioHeader audioHeader = audioFile.getAudioHeader();
            Tag tag = audioFile.getTag();

            String artist = tag.getFirst(org.jaudiotagger.tag.FieldKey.ARTIST);
            String album = tag.getFirst(org.jaudiotagger.tag.FieldKey.ALBUM);
            String title = tag.getFirst(org.jaudiotagger.tag.FieldKey.TITLE);

            artist = (artist == null || artist.isEmpty()) ? "Unknown Artist" : artist;
            album = (album == null || album.isEmpty()) ? "Unknown Album" : album;
            title = (title == null || title.isEmpty()) ? songFile.getName() : title;

            return new AudioMetadata(
                    audioHeader.getFormat(),
                    audioHeader.getBitRate(),
                    audioHeader.getSampleRate(),
                    artist,
                    album,
                    title
            );
        } catch (Exception e) {
            return new AudioMetadata(
                    "Unknown Format",
                    "Unknown Bitrate",
                    "Unknown Sample Rate",
                    "Unknown Artist",
                    "Unknown Album",
                    songFile.getName()
            );
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }
}
