package com.example.jambo.model;

public class PlayerState {
    private String currentSongTitle;
    private double currentPosition;
    private double totalDuration;
    private double volume;
    private boolean isPlaying;
    private boolean isLooping;
    private boolean isMuted;
    private int currentSongIndex;

    public PlayerState(double initialVolume) {
        this.volume = initialVolume;
        this.currentSongIndex = -1;
    }

    public String getCurrentSongTitle() {
        return currentSongTitle;
    }

    public void setCurrentSongTitle(String currentSongTitle) {
        this.currentSongTitle = currentSongTitle;
    }

    public double getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(double currentPosition) {
        this.currentPosition = currentPosition;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void togglePlaying() {
        isPlaying = !isPlaying;
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void toggleLoop() {
        isLooping = !isLooping;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void toggleMute() {
        isMuted = !isMuted;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        if (volume >= 0 && volume <= 1) {
            this.volume = volume;
        }
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }
}
