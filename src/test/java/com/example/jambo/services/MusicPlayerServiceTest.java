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

        // Initialize slider and set default value
        volumeSlider = new Slider(0, 1, 0.5);

        // Create mock MediaPlayer and Media
        mockMediaPlayer = mock(MediaPlayer.class);
        mockMedia = mock(Media.class);

        // Setup common mock behaviors
        doNothing().when(mockMediaPlayer).play();
        doNothing().when(mockMediaPlayer).pause();
        doNothing().when(mockMediaPlayer).stop();
        doNothing().when(mockMediaPlayer).setVolume(anyDouble());
        doNothing().when(mockMediaPlayer).seek(any(Duration.class));
        when(mockMediaPlayer.getTotalDuration()).thenReturn(Duration.seconds(180));

        // Initialize service with mocked MediaPlayer
        musicPlayerService = spy(new MusicPlayerService(volumeSlider, mockMediaPlayer));

        // Clear any invocations from initialization
        clearInvocations(mockMediaPlayer);
    }

    @Test
    public void testPlayMedia() {
        // Create a new MediaPlayer for the new Media
        MediaPlayer newMediaPlayer = mock(MediaPlayer.class);
        doReturn(newMediaPlayer).when(musicPlayerService).getMediaPlayer();

        // Play the media
        musicPlayerService.playMedia(mockMedia);

        // Verify the new MediaPlayer is set up correctly
        verify(newMediaPlayer, timeout(1000)).setVolume(volumeSlider.getValue());
    }

    @Test
    public void testVolumeControl() {
        // Set volume through slider
        volumeSlider.setValue(0.5);

        // Play media to trigger volume setting
        musicPlayerService.playMedia(mockMedia);

        // Verify volume was set
        verify(mockMediaPlayer, atLeastOnce()).setVolume(0.5);
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