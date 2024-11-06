package com.example.jambo.di;

import com.example.jambo.Interface.IDialogService;
import com.example.jambo.Interface.IMetadataService;
import com.example.jambo.Interface.IMusicPlayerService;
import com.example.jambo.Interface.IPlaylistService;
import com.example.jambo.services.DialogService;
import com.example.jambo.services.MetadataService;
import com.example.jambo.services.MusicPlayerService;
import com.example.jambo.services.PlaylistService;
import com.example.jambo.ui.IconService;
import javafx.scene.control.Slider;

public class DependencyContainer {
    private static IMusicPlayerService musicPlayerService;
    private static IMetadataService metadataService;
    private static IPlaylistService playlistService;
    public static IDialogService dialogService;
    private static IconService iconService;

    public static void initialize(Slider volumeSlider) {
        musicPlayerService = new MusicPlayerService(volumeSlider);
        metadataService = new MetadataService();
        playlistService = new PlaylistService();
        dialogService = new DialogService();
        iconService = new IconService();
    }

    public static IMusicPlayerService getMusicPlayerService() {
        return musicPlayerService;
    }

    public static IMetadataService getMetadataService() {
        return metadataService;
    }

    public static IPlaylistService getPlaylistService() {
        return playlistService;
    }

    public static IDialogService getDialogService() {
        return dialogService;
    }

    public static IconService getIconService() {
        return iconService;
    }
}