package com.example.jambo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.example.jambo.Jambo;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import static org.mockito.Mockito.*;

public class MusicPlayerServiceTest extends ApplicationTest {
    private MusicPlayerService musicPlayerService;
    private MediaPlayer mockMediaPlayer;
    private Slider volumeSlider;
    private Media mockMedia;

    @BeforeAll
    public static void initJavaFX() throws Exception {
        ApplicationTest.launch(Jambo.class);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize slider and set a default value
        volumeSlider = new Slider(0, 1, 0.5);

        // Mock MediaPlayer and Media
        mockMediaPlayer = mock(MediaPlayer.class);
        mockMedia = mock(Media.class);

        // Inject the mock MediaPlayer directly
        musicPlayerService = new MusicPlayerService(volumeSlider, mockMediaPlayer);

        // Set up common behaviors for the mock media player
        when(mockMediaPlayer.getMedia()).thenReturn(mockMedia);
        doNothing().when(mockMediaPlayer).play();
        doNothing().when(mockMediaPlayer).pause();
        doNothing().when(mockMediaPlayer).stop();
        when(mockMediaPlayer.getTotalDuration()).thenReturn(Duration.seconds(180));
        doNothing().when(mockMediaPlayer).setVolume(anyDouble());
        doNothing().when(mockMediaPlayer).seek(any(Duration.class));
    }

    @Test
    public void testVolumeControl() {
        // Adjust the volume on the slider
        volumeSlider.setValue(0.7);

        // Trigger volume change by calling playMedia()
        musicPlayerService.playMedia(mockMedia);

        // Verify that volume has been set as expected
        verify(mockMediaPlayer, atLeastOnce()).setVolume(0.7);
    }


    @Test
    public void testMuteToggle() {
        // Test mute
        musicPlayerService.toggleMute();
        verify(mockMediaPlayer).setVolume(0);

        // Clear previous invocations
        clearInvocations(mockMediaPlayer);

        // Test unmute
        musicPlayerService.toggleMute();
        verify(mockMediaPlayer).setVolume(volumeSlider.getValue());
    }

    @Test
    public void testPauseAndResumeMedia() {
        // Test pause
        musicPlayerService.pauseMedia();
        verify(mockMediaPlayer).pause();

        // Test resume
        musicPlayerService.pauseMedia();
        verify(mockMediaPlayer).play();
    }

    @Test
    public void testStopMedia() {
        musicPlayerService.stopMedia();
        verify(mockMediaPlayer).stop();
    }

    @Test
    public void testToggleLoop() {
        // Test enabling loop
        musicPlayerService.toggleLoop();
        verify(mockMediaPlayer).setCycleCount(MediaPlayer.INDEFINITE);

        // Test disabling loop
        musicPlayerService.toggleLoop();
        verify(mockMediaPlayer).setCycleCount(1);
    }

    @Test
    public void testSeekToPosition() {
        double seekTime = 30.0;
        Duration expectedDuration = Duration.seconds(seekTime);

        musicPlayerService.seekTo(seekTime);
        verify(mockMediaPlayer).seek(expectedDuration);
    }

    @Test
    public void testGetTotalDuration() {
        Duration expectedDuration = Duration.seconds(180);
        when(mockMediaPlayer.getTotalDuration()).thenReturn(expectedDuration);

        Duration actualDuration = musicPlayerService.getTotalDuration();
        assertEquals(expectedDuration, actualDuration);
    }
}