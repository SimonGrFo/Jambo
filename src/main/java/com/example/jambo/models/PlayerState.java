package com.example.jambo.models;

public class PlayerState {
    private boolean isPlaying;
    private boolean isLooping;
    private boolean isMuted;
    private double currentPosition;
    private double volume;
    private int currentSongIndex;

    public PlayerState(double initialVolume) {
        this.volume = initialVolume;
        this.currentSongIndex = -1;
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

    public double getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(double currentPosition) {
        if (currentPosition >= 0) {
            this.currentPosition = currentPosition;
        }
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
        if (currentSongIndex >= -1) {
            this.currentSongIndex = currentSongIndex;
        }
    }
}