package com.example.jambo.Interfaces;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public interface MusicPlayerInterface {
    void playMedia(Media media);
    void pauseMedia();
    void stopMedia();
    void toggleLoop();
    void seekTo(double time);
    MediaPlayer getMediaPlayer();

    boolean isLooping();

    Duration getTotalDuration();
}