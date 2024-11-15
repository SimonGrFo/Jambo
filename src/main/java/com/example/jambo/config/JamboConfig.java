package com.example.jambo.config;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import com.example.jambo.services.*;
import com.example.jambo.managers.*;
import com.example.jambo.ui.JamboUI;

@Configuration
@ComponentScan(basePackages = "com.example.jambo")
public class JamboConfig {

    @Bean
    @Primary
    public Slider progressSlider() {
        Slider slider = new Slider(0, 1, 0);
        slider.setShowTickLabels(true);
        return slider;
    }

    @Bean(name = "volumeSlider")
    public Slider volumeSlider() {
        Slider slider = new Slider(0, 1, 0.5);
        slider.setShowTickLabels(true);
        return slider;
    }

    @Bean
    public MusicPlayerService musicPlayerService(
            @Qualifier("progressSlider") Slider progressSlider,
            @Qualifier("volumeSlider") Slider volumeSlider) {
        return new MusicPlayerService(volumeSlider);
    }

    @Bean
    public PlaylistService playlistService() {
        return new PlaylistService();
    }

    @Bean
    public MetadataService metadataService() {
        return new MetadataService();
    }

    @Bean
    public IconService iconService() {
        return new IconService();
    }

    @Bean
    public DialogService dialogService(MetadataService metadataService) {
        return new DialogService(metadataService);
    }

    @Bean(name = "currentSongLabel")
    public Label currentSongLabel() {
        return new Label("Current Song");
    }

    @Bean(name = "timerLabel")
    public Label timerLabel() {
        return new Label("Timer");
    }

    @Bean(name = "fileInfoLabel")
    public Label fileInfoLabel() {
        return new Label("File Info");
    }

    @Bean
    public Pane albumArtPane() {
        return new Pane();
    }

    @Bean
    public MetadataManager metadataManager(
            MetadataService metadataService,
            @Qualifier("fileInfoLabel") Label fileInfoLabel,
            @Qualifier("currentSongLabel") Label currentSongLabel,
            Pane albumArtPane) {
        return new MetadataManager(metadataService, fileInfoLabel,
                currentSongLabel, albumArtPane);
    }

    @Bean
    public MusicPlayerManager musicPlayerManager(
            MusicPlayerService musicPlayerService,
            @Qualifier("currentSongLabel") Label currentSongLabel,
            @Qualifier("timerLabel") Label timerLabel,
            @Qualifier("progressSlider") Slider progressSlider) {
        return new MusicPlayerManager(musicPlayerService, currentSongLabel,
                timerLabel, progressSlider);
    }

    @Bean
    public PlaylistManager playlistManager(
            PlaylistService playlistService,
            ListView<String> playlistView) {
        return new PlaylistManager(playlistService, playlistView);
    }

    @Bean
    public JamboUI jamboUI(
            IconService iconService,
            DialogService dialogService,
            @Qualifier("progressSlider") Slider progressSlider,
            @Qualifier("volumeSlider") Slider volumeSlider,
            @Qualifier("currentSongLabel") Label currentSongLabel,
            @Qualifier("timerLabel") Label timerLabel,
            @Qualifier("fileInfoLabel") Label fileInfoLabel,
            Pane albumArtPane,
            ListView<String> playlistView) {
        return new JamboUI(iconService, dialogService, progressSlider, volumeSlider,
                currentSongLabel, timerLabel, fileInfoLabel, albumArtPane,
                playlistView);
    }

    @Bean
    public ListView<String> playlistView() {
        return new ListView<>();
    }
}