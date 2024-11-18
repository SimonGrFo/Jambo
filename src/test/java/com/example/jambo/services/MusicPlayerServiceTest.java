package com.example.jambo.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import com.example.jambo.controllers.VolumeController;
import com.example.jambo.event.MediaEventHandler;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MusicPlayerServiceTest {

    @Mock
    private VolumeController volumeController;

    @Mock
    private MediaEventHandler eventHandler;

    @Mock
    private Media media;

    @Mock
    private MediaPlayer mediaPlayer;

    private MusicPlayerService musicPlayerService;

    @BeforeEach
    void setUp() {
        musicPlayerService = new TestMusicPlayerService(volumeController, eventHandler);
    }

    private class TestMusicPlayerService extends MusicPlayerService {
        public TestMusicPlayerService(VolumeController volumeController, MediaEventHandler eventHandler) {
            super(volumeController, eventHandler);
        }

        @Override
        protected MediaPlayer createMediaPlayer(Media media) {
            return mediaPlayer;
        }
    }

    @Test
    void playMedia_ShouldInitializeAndPlayNewMediaPlayer() {
        musicPlayerService.playMedia(media);

        verify(volumeController).bindToMediaPlayer(mediaPlayer);
        verify(eventHandler).initializeEventHandlers(mediaPlayer);
        verify(mediaPlayer).setCycleCount(1);
        verify(mediaPlayer).play();
    }

    @Test
    void playMedia_WhenExistingMediaPlayer_ShouldDisposeAndCreateNew() {
        musicPlayerService.playMedia(media);

        Media newMedia = mock(Media.class);
        musicPlayerService.playMedia(newMedia);

        verify(mediaPlayer).dispose();
        verify(volumeController, times(2)).bindToMediaPlayer(mediaPlayer);
        verify(eventHandler, times(2)).initializeEventHandlers(mediaPlayer);
        verify(mediaPlayer, times(2)).play();
    }

    @Test
    void pauseMedia_WhenPlaying_ShouldPause() {
        musicPlayerService.playMedia(media);

        musicPlayerService.pauseMedia();

        verify(mediaPlayer).pause();
    }

    @Test
    void pauseMedia_WhenPaused_ShouldResume() {
        musicPlayerService.playMedia(media);
        musicPlayerService.pauseMedia();

        musicPlayerService.pauseMedia();

        verify(mediaPlayer, times(2)).play();
    }

    @Test
    void stopMedia_ShouldStopAndDisposeMediaPlayer() {
        musicPlayerService.playMedia(media);

        musicPlayerService.stopMedia();

        verify(mediaPlayer).stop();
        verify(mediaPlayer).dispose();
    }

    @Test
    void stopMedia_WhenNoMediaPlayer_ShouldNotThrowException() {
        musicPlayerService.stopMedia();
    }
}