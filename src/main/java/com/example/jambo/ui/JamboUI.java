package com.example.jambo.ui;

import com.example.jambo.controllers.JamboController;
import com.example.jambo.di.DependencyContainer;
import com.example.jambo.services.DialogService;
import com.example.jambo.ui.dialogs.PlaylistDialog;
import com.example.jambo.ui.dialogs.SettingsDialog;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class JamboUI {
    private final ListView<String> songListView;
    private final Label currentSongLabel;
    private final Label timerLabel;
    private final Label fileInfoLabel;
    private final Slider progressSlider;
    private final Slider volumeSlider;
    private final ComboBox<String> playlistComboBox;
    private final IconService iconService;
    private final DialogService dialogService;

    public JamboUI() {
        this.songListView = new ListView<>();
        this.currentSongLabel = new Label("No song playing");
        this.timerLabel = new Label("0:00 / 0:00");
        this.fileInfoLabel = new Label("Format: - Hz, - kbps");
        this.progressSlider = new Slider(0, 1, 0);
        this.volumeSlider = new Slider(0, 1, 0.5);
        this.playlistComboBox = new ComboBox<>();

        DependencyContainer.initialize(this.volumeSlider);

        this.iconService = DependencyContainer.getIconService();
        this.dialogService = DependencyContainer.getDialogService();

        setupPlaylistComboBox();
    }

    private HBox createControlBox(JamboController controller) {
        Button playButton = new Button("", iconService.createIconImageView("play"));
        Tooltip.install(playButton, new Tooltip("Play"));

        Button pauseButton = new Button("", iconService.createIconImageView("pause"));
        Tooltip.install(pauseButton, new Tooltip("Pause"));

        Button stopButton = new Button("", iconService.createIconImageView("stop"));
        Tooltip.install(stopButton, new Tooltip("Stop"));

        Button previousButton = new Button("", iconService.createIconImageView("previous"));
        Tooltip.install(previousButton, new Tooltip("Previous Song"));

        Button nextButton = new Button("", iconService.createIconImageView("next"));
        Tooltip.install(nextButton, new Tooltip("Next Song"));

        Button shuffleButton = new Button("", iconService.createIconImageView("shuffle"));
        Tooltip.install(shuffleButton, new Tooltip("Shuffle"));

        Button loopButton = new Button("", iconService.createIconImageView("loop"));
        Tooltip.install(loopButton, new Tooltip("Loop"));

        Button muteButton = new Button("", iconService.createIconImageView("mute"));
        Tooltip.install(muteButton, new Tooltip("Mute"));

        Button clearButton = new Button("", iconService.createIconImageView("clear"));
        Tooltip.install(clearButton, new Tooltip("Clear Playlist"));

        Button settingsButton = new Button("", iconService.createIconImageView("settings"));
        Tooltip.install(settingsButton, new Tooltip("Settings"));

        Button playlistButton = new Button("", iconService.createIconImageView("playlist"));
        Tooltip.install(playlistButton, new Tooltip("playlist"));

        setupControlButtons(controller, playButton, pauseButton, stopButton,
                previousButton, nextButton, shuffleButton, loopButton, muteButton);

        HBox volumeBox = new HBox(10, volumeSlider, muteButton);
        HBox controlBox = new HBox(10, playButton, pauseButton, stopButton,
                previousButton, nextButton, shuffleButton, loopButton, volumeBox);
        HBox.setHgrow(controlBox, Priority.ALWAYS);

        return controlBox;
    }

    private void setupPlaylistComboBox() {
        playlistComboBox.setPromptText("Select Playlist");
        playlistComboBox.setPrefWidth(150);
    }

    public void initializeContextMenu(JamboController controller) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem removeItem = new MenuItem("Remove");
        removeItem.setOnAction(event -> {
            int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                controller.removeSong(selectedIndex);
            }
        });

        MenuItem propertiesItem = new MenuItem("Properties");
        propertiesItem.setOnAction(event -> {
            int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                dialogService.showPropertiesDialog(controller.getSongFile(selectedIndex));
            }
        });

        contextMenu.getItems().addAll(removeItem, propertiesItem);
        songListView.setContextMenu(contextMenu);
    }

    public Scene createScene(JamboController controller) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(createHeaderBox(controller));
        mainLayout.setCenter(songListView);
        mainLayout.setBottom(new VBox(10, createControlBox(controller), createProgressBox()));
        return new Scene(mainLayout, 800, 400);
    }

    private HBox createHeaderBox(JamboController controller) {
        Button loadButton = new Button("", iconService.createIconImageView("load songs"));
        Tooltip loadTooltip = new Tooltip("Load songs");
        Tooltip.install(loadButton, loadTooltip);

        Button clearButton = new Button("", iconService.createIconImageView("clear songs"));
        Tooltip clearTooltip = new Tooltip("Clear songs");
        Tooltip.install(clearButton, clearTooltip);

        Button playlistButton = new Button("", iconService.createIconImageView("playlist"));
        Tooltip playlistTooltip = new Tooltip("playlist");
        Tooltip.install(playlistButton, playlistTooltip);

        Button settingsButton = new Button("", iconService.createIconImageView("settings"));
        Tooltip settingsTooltip = new Tooltip("Settings"); // TODO - move this all the way to the right while keeping the other buttons on the left side
        Tooltip.install(settingsButton, settingsTooltip);


        loadButton.setOnAction(e -> controller.loadSongs());
        clearButton.setOnAction(e -> controller.clearSongs());
        playlistButton.setOnAction(e -> {
            PlaylistDialog dialog = new PlaylistDialog(controller);
            dialog.show();
        });
        settingsButton.setOnAction(e -> {
            SettingsDialog dialog = new SettingsDialog(controller);
            dialog.show();
        });

        HBox headerBox = new HBox(10);
        headerBox.setPadding(new Insets(10));
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(loadButton, clearButton, playlistButton, settingsButton);

        return headerBox;
    }

    private void setupControlButtons(JamboController controller, Button... buttons) {
        buttons[0].setOnAction(e -> controller.playSelectedSong());
        buttons[1].setOnAction(e -> controller.pauseMusic());
        buttons[2].setOnAction(e -> controller.stopMusic());
        buttons[3].setOnAction(e -> controller.playPreviousSong());
        buttons[4].setOnAction(e -> controller.playNextSong());
        buttons[5].setOnAction(e -> controller.toggleShuffle());
        buttons[6].setOnAction(e -> controller.toggleLoop());
        buttons[7].setOnAction(e -> controller.toggleMute());
    }

    private VBox createProgressBox() {
        HBox timeBox = new HBox(10, timerLabel, progressSlider);
        HBox.setHgrow(progressSlider, Priority.ALWAYS);
        return new VBox(5, timeBox, fileInfoLabel);
    }

    public ListView<String> getSongListView() { return songListView; }
    public Label getCurrentSongLabel() { return currentSongLabel; }
    public Label getTimerLabel() { return timerLabel; }
    public Label getFileInfoLabel() { return fileInfoLabel; }
    public Slider getProgressSlider() { return progressSlider; }
    public Slider getVolumeSlider() { return volumeSlider; }

}