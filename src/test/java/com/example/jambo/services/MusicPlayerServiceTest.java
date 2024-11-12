package com.example.jambo.services;

import static org.mockito.Mockito.*;

import com.example.jambo.Jambo;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

public class MusicPlayerServiceTest extends ApplicationTest {
    private MusicPlayerService musicPlayerService;
    private MediaPlayer mockMediaPlayer;
    private Slider volumeSlider;

    @BeforeAll
    public static void initJavaFX() throws Exception {
        // Start JavaFX application thread to initialize JavaFX runtime
        ApplicationTest.launch(Jambo.class);  // Dummy app initialization
    }

    @BeforeEach
    public void setUp() {
        volumeSlider = new Slider();
        mockMediaPlayer = mock(MediaPlayer.class);
        doNothing().when(mockMediaPlayer).play();
        doNothing().when(mockMediaPlayer).stop();
        doNothing().when(mockMediaPlayer).setVolume(anyDouble());

        // Set up service and clear initial setVolume calls
        musicPlayerService = new MusicPlayerService(volumeSlider, mockMediaPlayer);
        clearInvocations(mockMediaPlayer);  // Clear any setup interactions
    }

    @Test
    public void testPlayMedia() {
        Media mockMedia = mock(Media.class);
        musicPlayerService.playMedia(mockMedia);
        verify(mockMediaPlayer).play();
    }

    @Test
    public void testVolumeControl() {
        // Set up the slider to simulate user interaction
        volumeSlider.setValue(0.5);  // Set slider to 50%

        // Play a media item to initialize the mediaPlayer with current volume
        Media mockMedia = mock(Media.class);
        musicPlayerService.playMedia(mockMedia);

        // Verify if setVolume was called with 0.5
        verify(mockMediaPlayer, atLeastOnce()).setVolume(0.5);    }


    @Test
    public void testMuteToggle() {
        // Set up initial volume
        volumeSlider.setValue(0.5);  // Set slider to 50%
        doNothing().when(mockMediaPlayer).setVolume(anyDouble());

        // Mute the player
        musicPlayerService.toggleMute();
        verify(mockMediaPlayer).setVolume(0);  // Confirm volume set to 0 for mute

        // Unmute the player
        musicPlayerService.toggleMute();
        verify(mockMediaPlayer).setVolume(0.5);  // Confirm volume set back to slider's value for unmute
    }

}
