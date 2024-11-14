package com.example.jambo.dependency.injection;

import com.example.jambo.Interfaces.DialogInterface;
import com.example.jambo.Interfaces.MetadataInterface;
import com.example.jambo.Interfaces.MusicPlayerInterface;
import com.example.jambo.Interfaces.PlaylistInterface;
import com.example.jambo.services.DialogService;
import com.example.jambo.services.MetadataService;
import com.example.jambo.services.MusicPlayerService;
import com.example.jambo.services.PlaylistService;
import com.example.jambo.services.IconService;
import javafx.scene.control.Slider;

public class DependencyContainer {
    private static MusicPlayerInterface musicPlayer;
    private static MetadataInterface metadataService;
    private static PlaylistInterface playlistService;
    public static DialogInterface dialogService;
    private static IconService iconService;

    public static void initialize(Slider volumeSlider) {
        musicPlayer = new MusicPlayerService(volumeSlider);
        metadataService = new MetadataService();
        playlistService = new PlaylistService();
        dialogService = new DialogService(metadataService);
        iconService = new IconService();
    }

    public static MusicPlayerInterface getMusicPlayerService() {
        return musicPlayer;
    }

    public static MetadataInterface getMetadataService() {
        return metadataService;
    }

    public static PlaylistInterface getPlaylistService() {
        return playlistService;
    }

    public static IconService getIconService() {
        return iconService;
    }
}