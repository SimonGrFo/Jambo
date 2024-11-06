package com.example.jambo.services;

import com.example.jambo.Interfaces.MusicPlayerInterface;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Slider;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.util.prefs.Preferences;

public class MusicPlayerService implements MusicPlayerInterface {
    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;
    private boolean isLooping = false;
    private boolean isMuted = false;
    private final Slider volumeSlider;
    private final Preferences preferences;
    private Runnable onEndOfMedia;
    private double crossfadeDuration = 0;
    private MediaPlayer nextPlayer;

    public MusicPlayerService(Slider volumeSlider) {
        this.volumeSlider = volumeSlider;
        this.preferences = Preferences.userNodeForPackage(MusicPlayerService.class);
        setupVolumeControl();
        loadSettings();
    }

    private void loadSettings() {
        crossfadeDuration = preferences.getDouble("crossfade", 0);
    }

    public void setOnEndOfMedia(Runnable callback) {
        this.onEndOfMedia = callback;
    }

    private void setupVolumeControl() {
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null && !isMuted) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });
    }

    @Override
    public void playMedia(Media media) {
        double currentVolume = volumeSlider.getValue();

        if (mediaPlayer != null) {
            if (crossfadeDuration > 0) {
                crossfade(media);
            } else {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                setupNewPlayer(media, currentVolume);
            }
        } else {
            setupNewPlayer(media, currentVolume);
        }
    }

    private void setupNewPlayer(Media media, double currentVolume) {
        mediaPlayer = new MediaPlayer(media);
        setupMediaPlayer(mediaPlayer, currentVolume);
        mediaPlayer.play();
    }

    private void setupMediaPlayer(MediaPlayer player, double volume) {
        player.setOnError(() ->
                System.err.println("Media player error: " + player.getError().getMessage()));

        player.setVolume(isMuted ? 0 : volume);
        player.setCycleCount(isLooping ? MediaPlayer.INDEFINITE : 1);

        player.setOnEndOfMedia(() -> {
            if (!isLooping && onEndOfMedia != null) {
                onEndOfMedia.run();
            }
        });

        // Add audio settings
        player.setBalance(preferences.getDouble("balance", 0.0));
        player.setRate(preferences.getDouble("playbackSpeed", 1.0));

        // Add audio processing if enabled
        if (preferences.getBoolean("equalizer", false)) {
            setupEqualizer(player);
        }
    }

    private void crossfade(Media nextMedia) {
        nextPlayer = new MediaPlayer(nextMedia);
        setupMediaPlayer(nextPlayer, volumeSlider.getValue());

        // Fade out current player
        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new javafx.animation.KeyValue(mediaPlayer.volumeProperty(), mediaPlayer.getVolume())),
                new KeyFrame(Duration.seconds(crossfadeDuration),
                        new javafx.animation.KeyValue(mediaPlayer.volumeProperty(), 0))
        );

        // Fade in next player
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new javafx.animation.KeyValue(nextPlayer.volumeProperty(), 0)),
                new KeyFrame(Duration.seconds(crossfadeDuration),
                        new javafx.animation.KeyValue(nextPlayer.volumeProperty(), volumeSlider.getValue()))
        );

        fadeOut.setOnFinished(e -> {
            mediaPlayer.dispose();
            mediaPlayer = nextPlayer;
            nextPlayer = null;
        });

        nextPlayer.play();
        fadeOut.play();
        fadeIn.play();
    }




    @Override
    public void pauseMedia() {
        if (mediaPlayer != null) {
            if (isPaused) {
                mediaPlayer.play();
                isPaused = false;
            } else {
                mediaPlayer.pause();
                isPaused = true;
            }
        }
    }

    @Override
    public void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void toggleLoop() {
        isLooping = !isLooping;
        if (mediaPlayer != null) {
            mediaPlayer.setCycleCount(isLooping ? MediaPlayer.INDEFINITE : 1);
        }
    }

    @Override
    public void toggleMute() {
        isMuted = !isMuted;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(isMuted ? 0 : volumeSlider.getValue());
        }
    }

    @Override
    public void seekTo(double time) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(time));
        }
    }

    @Override
    public void setVolume(double volume) {
        if (mediaPlayer != null && !isMuted) {
            mediaPlayer.setVolume(volume);
        }
    }

    private void setupEqualizer(MediaPlayer player) {
        AudioEqualizer equalizer = player.getAudioEqualizer();
        EqualizerBand[] bands = new EqualizerBand[10];

        double[] centerFrequencies = {32, 64, 125, 250, 500, 1000, 2000, 4000, 8000, 16000};
        for (int i = 0; i < bands.length; i++) {
            bands[i] = new EqualizerBand();
            bands[i].setCenterFrequency(centerFrequencies[i]);
            bands[i].setGain(preferences.getDouble("eq_band_" + i, 0.0));
            equalizer.getBands().add(bands[i]);
        }
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public Duration getTotalDuration() {
        return mediaPlayer != null ? mediaPlayer.getTotalDuration() : Duration.ZERO;
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }


}
