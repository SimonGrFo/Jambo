package com.example.jambo.Interface;

public interface IMusicPlayerManager {
    void playMedia(javafx.scene.media.Media media);
    void pauseMusic();
    void stopMusic();
    void toggleLoop();
    void toggleMute();
    void seekTo(double time);
    double getTotalDuration();
}