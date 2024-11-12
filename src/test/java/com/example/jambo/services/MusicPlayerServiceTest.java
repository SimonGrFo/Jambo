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
import org.testfx.framework.junit5.ApplicationTest;

public class MusicPlayerServiceTest extends ApplicationTest {
    private MusicPlayerService musicPlayerService;
    private MediaPlayer mockMediaPlayer;
    private Slider volumeSlider;

    @BeforeAll
    public static void initJavaFX() throws Exception { //test
        ApplicationTest.launch(Jambo.class);
    }

    @BeforeEach
    public void setUp() {
        volumeSlider = new Slider();
        mockMediaPlayer = mock(MediaPlayer.class);
        doNothing().when(mockMediaPlayer).play();
        doNothing().when(mockMediaPlayer).stop();
        doNothing().when(mockMediaPlayer).setVolume(anyDouble());

        musicPlayerService = new MusicPlayerService(volumeSlider, mockMediaPlayer);
        clearInvocations(mockMediaPlayer);
    }

    @Test
    public void testPlayMedia() {
        Media mockMedia = mock(Media.class);
        musicPlayerService.playMedia(mockMedia);
        verify(mockMediaPlayer).play();
    }

    @Test
    public void testVolumeControl() {
        volumeSlider.setValue(0.5);

        Media mockMedia = mock(Media.class);
        musicPlayerService.playMedia(mockMedia);

        verify(mockMediaPlayer, atLeastOnce()).setVolume(0.5);    }


    @Test
    public void testMuteToggle() {
        clearInvocations(mockMediaPlayer);

        musicPlayerService.toggleMute();
        verify(mockMediaPlayer).setVolume(0);

        clearInvocations(mockMediaPlayer);

        musicPlayerService.toggleMute();
        verify(mockMediaPlayer).setVolume(volumeSlider.getValue());
    }
    @Test
    public void testPauseAndResumeMedia() {
        musicPlayerService.pauseMedia();
        verify(mockMediaPlayer).pause();

        musicPlayerService.pauseMedia();
        verify(mockMediaPlayer).play();
    }

    @Test
    public void testStopMedia() {
        // Test stopping functionality
        musicPlayerService.stopMedia();
        verify(mockMediaPlayer).stop();
    }

    @Test
    public void testToggleLoop() {
        musicPlayerService.toggleLoop();
        verify(mockMediaPlayer).setCycleCount(MediaPlayer.INDEFINITE);

        musicPlayerService.toggleLoop();
        verify(mockMediaPlayer).setCycleCount(1);
    }

    @Test
    public void testSeekToPosition() {
        double seekTime = 30.0;
        Duration duration = Duration.seconds(seekTime);

        musicPlayerService.seekTo(seekTime);
        verify(mockMediaPlayer).seek(duration);
    }

    @Test
    public void testGetTotalDuration() {
        Duration duration = Duration.seconds(180);
        when(mockMediaPlayer.getTotalDuration()).thenReturn(duration);

        Duration result = musicPlayerService.getTotalDuration();
        assertEquals(duration, result);
    }
}
