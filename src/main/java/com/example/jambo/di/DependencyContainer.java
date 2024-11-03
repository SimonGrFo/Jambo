package com.example.jambo.di;

import com.example.jambo.services.DialogService;
import com.example.jambo.services.MetadataService;
import com.example.jambo.services.MusicPlayerService;
import com.example.jambo.services.PlaylistService;
import com.example.jambo.ui.IconService;
import javafx.scene.control.Slider;

public class DependencyContainer {
    private static MusicPlayerService musicPlayerService;
    private static MetadataService metadataService;
    private static PlaylistService playlistService;
    private static DialogService dialogService;
    private static IconService iconService;

    public static void initialize(Slider volumeSlider) {
        musicPlayerService = new MusicPlayerService(volumeSlider);
        metadataService = new MetadataService();
        playlistService = new PlaylistService();
        dialogService = new DialogService();
        iconService = new IconService();
    }

    public static MusicPlayerService getMusicPlayerService() {
        return musicPlayerService;
    }

    public static MetadataService getMetadataService() {
        return metadataService;
    }

    public static PlaylistService getPlaylistService() {
        return playlistService;
    }

    public static DialogService getDialogService() {
        return dialogService;
    }

    public static IconService getIconService() {
        return iconService;
    }
}