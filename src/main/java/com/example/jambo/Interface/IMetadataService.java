package com.example.jambo.Interface;


import java.io.File;

public interface IMetadataService {
    String formatSongMetadata(File file) throws Exception;
    AudioMetadata getFileMetadata(File file) throws Exception;

    public class AudioMetadata {
        public final String format;
        public final String bitRate;
        public final String sampleRate;
        public final String artist;
        public final String album;
        public final String title;


        public AudioMetadata(String format, String bitRate, String sampleRate,
                             String artist, String album, String title) {
            this.format = format;
            this.bitRate = bitRate;
            this.sampleRate = sampleRate;
            this.artist = artist;
            this.album = album;
            this.title = title;
        }
    }
}