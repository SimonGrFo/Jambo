package com.example.jambo.Interface;

import javafx.scene.media.MediaPlayer;

public interface IMusicPlayerService {
    void playMedia(javafx.scene.media.Media media);
    void pauseMusic();
    void stopMusic();
    void toggleLoop();
    void toggleMute();
    void seekTo(double time);
    MediaPlayer getMediaPlayer();
}