package com.example.jambo.ui;

import com.example.jambo.controllers.JamboController;
import com.example.jambo.services.MetadataService;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

import java.io.File;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class JamboUI {
    private final ListView<String> songListView;
    private final Label currentSongLabel;
    private final Label timerLabel;
    private final Label fileInfoLabel;
    private final Slider progressSlider;
    private final Slider volumeSlider;
    private final ImageView albumArtView;
    private final ComboBox<String> playlistComboBox;

    private final Image playIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/play_icon.png")));
    private final Image pauseIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/pause_icon.png")));
    private final Image stopIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/stop_icon.png")));
    private final Image previousIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/previous_icon.png")));
    private final Image nextIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/next_icon.png")));
    private final Image loopIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/loop_icon.png")));
    private final Image shuffleIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/shuffle_icon.png")));
    private final Image muteIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/icons/mute_icon.png")));

    public JamboUI() {
        songListView = new ListView<>();
        currentSongLabel = new Label("No song playing");
        timerLabel = new Label("0:00 / 0:00");
        fileInfoLabel = new Label("Format: - Hz, - kbps");
        progressSlider = new Slider(0, 1, 0);
        volumeSlider = new Slider(0, 1, 0.5);
        albumArtView = new ImageView();
        playlistComboBox = new ComboBox<>();

        setupAlbumArtView();
        setupPlaylistComboBox();
    }

    public void initializeContextMenu(JamboController controller) {
        setupContextMenu(controller);
    }

    private void setupAlbumArtView() {
        albumArtView.setFitWidth(100);
        albumArtView.setFitHeight(100);
        albumArtView.setPreserveRatio(true);
    }

    private void setupPlaylistComboBox() {
        playlistComboBox.setPromptText("Select Playlist");
        playlistComboBox.setPrefWidth(150);
    }

    private void setupContextMenu(JamboController controller) {
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
                showPropertiesDialog(controller.getSongFile(selectedIndex));
            }
        });

        contextMenu.getItems().addAll(removeItem, propertiesItem);

        songListView.setContextMenu(contextMenu);
    }

    private void showPropertiesDialog(File file) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("File Properties");
        dialog.setHeaderText("Properties for " + file.getName());

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        try {
            grid.add(new Label("Location:"), 0, 0);
            grid.add(new Label(file.getAbsolutePath()), 1, 0);

            grid.add(new Label("Size:"), 0, 1);
            grid.add(new Label(formatFileSize(file.length())), 1, 1);

            grid.add(new Label("Last Modified:"), 0, 2);
            grid.add(new Label(new Date(file.lastModified()).toString()), 1, 2);

            MetadataService.AudioMetadata metadata = new MetadataService().getFileMetadata(file);

            grid.add(new Label("Format:"), 0, 3);
            grid.add(new Label(metadata.format), 1, 3);

            grid.add(new Label("Bit Rate:"), 0, 4);
            grid.add(new Label(metadata.bitRate + " kbps"), 1, 4);

            grid.add(new Label("Sample Rate:"), 0, 5);
            grid.add(new Label(metadata.sampleRate + " Hz"), 1, 5);

            grid.add(new Label("Artist:"), 0, 6);
            grid.add(new Label(metadata.artist), 1, 6);

            grid.add(new Label("Album:"), 0, 7);
            grid.add(new Label(metadata.album), 1, 7);

            grid.add(new Label("Title:"), 0, 8);
            grid.add(new Label(metadata.title), 1, 8);

        } catch (Exception e) {
            grid.add(new Label("Error reading metadata: " + e.getMessage()), 0, 9, 2, 1);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private ImageView createIconImageView(Image icon) {
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        return imageView;
    }

    public Scene createScene(JamboController controller) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(createHeaderBox(controller));

        SplitPane centerPane = new SplitPane();
        centerPane.getItems().addAll(
                createPlaylistManagementPane(controller),
                songListView
        );
        centerPane.setDividerPositions(0.2);

        mainLayout.setCenter(centerPane);
        mainLayout.setBottom(new VBox(10, createControlBox(controller), createProgressBox()));

        return new Scene(mainLayout, 800, 600);
    }

    private VBox createPlaylistManagementPane(JamboController controller) {
        VBox playlistPane = new VBox(10);
        playlistPane.setPadding(new Insets(10));

        Label playlistLabel = new Label("Current Playlist:");

        playlistComboBox.getItems().addAll(controller.getPlaylistNames());
        playlistComboBox.setValue(controller.getCurrentPlaylistName());
        playlistComboBox.setOnAction(e -> {
            String selectedPlaylist = playlistComboBox.getValue();
            if (selectedPlaylist != null) {
                controller.switchPlaylist(selectedPlaylist);
            }
        });

        Button newPlaylistButton = new Button("New Playlist");
        Button deletePlaylistButton = new Button("Delete Playlist");

        newPlaylistButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Playlist");
            dialog.setHeaderText("Create a new playlist");
            dialog.setContentText("Enter playlist name:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                controller.createPlaylist(name);
                playlistComboBox.getItems().add(name);
                playlistComboBox.setValue(name);
            });
        });

        deletePlaylistButton.setOnAction(e -> {
            String selectedPlaylist = playlistComboBox.getValue();
            if (selectedPlaylist != null && !selectedPlaylist.equals("Default")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Playlist");
                alert.setHeaderText("Delete playlist: " + selectedPlaylist);
                alert.setContentText("Are you sure? This cannot be undone.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    controller.deletePlaylist(selectedPlaylist);
                    playlistComboBox.getItems().remove(selectedPlaylist);
                    playlistComboBox.setValue("Default");
                }
            }
        });

        newPlaylistButton.setMaxWidth(Double.MAX_VALUE);
        deletePlaylistButton.setMaxWidth(Double.MAX_VALUE);

        playlistPane.getChildren().addAll(
                playlistLabel,
                playlistComboBox,
                newPlaylistButton,
                deletePlaylistButton
        );

        return playlistPane;
    }

    private HBox createHeaderBox(JamboController controller) {
        Button loadButton = new Button("Load Songs");
        Button clearButton = new Button("Clear Songs");

        loadButton.setOnAction(e -> controller.loadSongs());
        clearButton.setOnAction(e -> controller.clearSongs());

        return new HBox(10, loadButton, clearButton);
    }

    private HBox createControlBox(JamboController controller) {
        Button playButton = new Button("", createIconImageView(playIcon));
        Button pauseButton = new Button("", createIconImageView(pauseIcon));
        Button stopButton = new Button("", createIconImageView(stopIcon));
        Button previousButton = new Button("", createIconImageView(previousIcon));
        Button nextButton = new Button("", createIconImageView(nextIcon));
        Button shuffleButton = new Button("", createIconImageView(shuffleIcon));
        Button loopButton = new Button("", createIconImageView(loopIcon));
        Button muteButton = new Button("", createIconImageView(muteIcon));

        playButton.setOnAction(e -> controller.playSelectedSong());
        pauseButton.setOnAction(e -> controller.pauseMusic());
        stopButton.setOnAction(e -> controller.stopMusic());
        previousButton.setOnAction(e -> controller.playPreviousSong());
        nextButton.setOnAction(e -> controller.playNextSong());
        shuffleButton.setOnAction(e -> controller.toggleShuffle());
        loopButton.setOnAction(e -> controller.toggleLoop());
        muteButton.setOnAction(e -> controller.toggleMute());

        HBox volumeBox = new HBox(10, volumeSlider, muteButton);
        HBox controlBox = new HBox(10, playButton, pauseButton, stopButton,
                previousButton, nextButton, shuffleButton,
                loopButton, volumeBox);
        HBox.setHgrow(controlBox, Priority.ALWAYS);

        return new HBox(20, albumArtView, controlBox);
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
    public ImageView getAlbumArtView() { return albumArtView; }
    public ComboBox<String> getPlaylistComboBox() { return playlistComboBox; }

    public void refreshPlaylistComboBox(JamboController controller) {
        playlistComboBox.getItems().clear();
        playlistComboBox.getItems().addAll(controller.getPlaylistNames());
        playlistComboBox.setValue(controller.getCurrentPlaylistName());
    }
}