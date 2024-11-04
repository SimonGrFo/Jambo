package com.example.jambo.services;

import com.example.jambo.exceptions.AudioPlayerException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class MetadataService {
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

    public AudioMetadata getFileMetadata(File songFile) throws AudioPlayerException {
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
            throw new AudioPlayerException(
                    AudioPlayerException.ErrorType.METADATA_ERROR,
                    "Error reading metadata from file: " + songFile.getName(),
                    e
            );
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    public class AudioMetadata {
        public final String format;
        public final String bitRate;
        public final String sampleRate;
        public final String artist;
        public final String album;
        public final String title;

        public AudioMetadata(String format, String bitRate, String sampleRate, String artist, String album, String title) {
            this.format = format;
            this.bitRate = bitRate;
            this.sampleRate = sampleRate;
            this.artist = artist;
            this.album = album;
            this.title = title;
        }
    }
}
