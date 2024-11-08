package com.example.jambo.ui;

import com.example.jambo.controllers.JamboController;
import com.example.jambo.dependency.injection.DependencyContainer;
import com.example.jambo.services.IconService;
import com.example.jambo.ui.dialogs.PlaylistDialog;
import com.example.jambo.ui.dialogs.SettingsDialog;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

import static com.example.jambo.dependency.injection.DependencyContainer.dialogService;

public class JamboUI {
    private final ListView<String> songListView;
    private final Label currentSongLabel;
    private final Label timerLabel;
    private final Label fileInfoLabel;
    private final Slider progressSlider;
    private final Slider volumeSlider;
    private final ComboBox<String> playlistComboBox;
    private final IconService iconService;
    private final Pane albumArtPlaceholder;

    public JamboUI() {
        this.songListView = new ListView<>();
        this.currentSongLabel = new Label("No song playing");
        this.timerLabel = new Label("0:00 / 0:00");
        this.fileInfoLabel = new Label("Format: - Hz, - kbps");
        this.progressSlider = new Slider(0, 1, 0);
        this.volumeSlider = new Slider(0, 1, 0.5);
        this.playlistComboBox = new ComboBox<>();
        this.albumArtPlaceholder = new Pane();

        albumArtPlaceholder.setStyle("-fx-background-color: #D3D3D3; -fx-border-color: #808080;");
        albumArtPlaceholder.setPrefSize(100, 100);
        albumArtPlaceholder.setMaxSize(100, 100);
        albumArtPlaceholder.setMinSize(100, 100);

        currentSongLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        currentSongLabel.setMaxWidth(Double.MAX_VALUE);
        currentSongLabel.setAlignment(Pos.CENTER);

        DependencyContainer.initialize(this.volumeSlider);

        this.iconService = DependencyContainer.getIconService();

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

        setupControlButtons(controller, playButton, pauseButton, stopButton,
                previousButton, nextButton, shuffleButton, loopButton);

        HBox controlBox = new HBox(10, playButton, pauseButton, stopButton,
                previousButton, nextButton, shuffleButton, loopButton);

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

        VBox bottomContainer = new VBox(10);
        bottomContainer.setPadding(new Insets(10));

        bottomContainer.getChildren().add(currentSongLabel);

        HBox bottomBox = new HBox(10);
        bottomBox.getChildren().add(albumArtPlaceholder);

        VBox controlsAndProgress = new VBox(10);
        controlsAndProgress.getChildren().addAll(
                createControlBox(controller),
                createProgressBox()
        );
        HBox.setHgrow(controlsAndProgress, Priority.ALWAYS);

        bottomBox.getChildren().add(controlsAndProgress);
        bottomContainer.getChildren().add(bottomBox);

        mainLayout.setBottom(bottomContainer);

        return new Scene(mainLayout, 800, 400);
    }

    private HBox createHeaderBox(JamboController controller) {
        Button loadButton = new Button("", iconService.createIconImageView("load songs"));
        Tooltip.install(loadButton, new Tooltip("Load folder"));

        Button clearButton = new Button("", iconService.createIconImageView("clear songs"));
        Tooltip.install(clearButton, new Tooltip("Clear songs"));

        Button playlistButton = new Button("", iconService.createIconImageView("playlist"));
        Tooltip.install(playlistButton, new Tooltip("Playlist"));

        Button settingsButton = new Button("", iconService.createIconImageView("settings"));
        Tooltip.install(settingsButton, new Tooltip("Settings"));

        Button muteButton = new Button("", iconService.createIconImageView("mute"));
        Tooltip.install(muteButton, new Tooltip("Mute"));

        muteButton.setOnAction(e -> controller.toggleMute());
        volumeSlider.setPrefWidth(100);
        HBox volumeBox = new HBox(5, muteButton, volumeSlider);

        loadButton.setOnAction(e -> controller.loadSongs());
        clearButton.setOnAction(e -> controller.clearSongs());
        playlistButton.setOnAction(e -> new PlaylistDialog(controller).show());
        settingsButton.setOnAction(e -> new SettingsDialog().show());

        HBox headerBox = new HBox(10);
        headerBox.setPadding(new Insets(10));
        headerBox.setAlignment(Pos.CENTER_LEFT);

        HBox leftButtonsBox = new HBox(10, loadButton, clearButton, playlistButton);
        HBox.setHgrow(leftButtonsBox, Priority.ALWAYS);

        headerBox.getChildren().addAll(leftButtonsBox, volumeBox, settingsButton);
        headerBox.setAlignment(Pos.CENTER);

        return headerBox;
    }


    private void setupControlButtons(JamboController controller, Button playButton, Button pauseButton, Button stopButton,
                                     Button previousButton, Button nextButton, Button shuffleButton, Button loopButton) {
        playButton.setOnAction(e -> controller.playSelectedSong());
        pauseButton.setOnAction(e -> controller.pauseMusic());
        stopButton.setOnAction(e -> controller.stopMusic());
        previousButton.setOnAction(e -> controller.playPreviousSong());
        nextButton.setOnAction(e -> controller.playNextSong());
        shuffleButton.setOnAction(e -> controller.toggleShuffle());
        loopButton.setOnAction(e -> controller.toggleLoop());
    }

    private VBox createProgressBox() {
        HBox timeBox = new HBox(10, timerLabel, progressSlider);
        HBox.setHgrow(progressSlider, Priority.ALWAYS);
        return new VBox(5, timeBox, fileInfoLabel);
    }

    public Pane getAlbumArtPlaceholder() {
        return albumArtPlaceholder;
    }

    public ListView<String> getSongListView() { return songListView; }
    public Label getCurrentSongLabel() { return currentSongLabel; }
    public Label getTimerLabel() { return timerLabel; }
    public Label getFileInfoLabel() { return fileInfoLabel; }
    public Slider getProgressSlider() { return progressSlider; }
    public Slider getVolumeSlider() { return volumeSlider; }

}