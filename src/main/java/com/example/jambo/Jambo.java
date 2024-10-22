package com.example.jambo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// TODO BIG TODOS!!!!
// TODO -
// TODO -   make it more oop, super cluttered right now.
// TODO -   fix styling its super ugly at the moment
// TODO -   bugs! they are in trello! but i already know this! if you're reading this
//          documentation and you arent me, Hi!
// TODO -   figure out how the metadata, its hard
// TODO -   load multiple songs from different folders
// TODO -   album art
// TODO -   make audio match when another song plays, currently volume goes back to
//          default, but the slider stays the same
// TODO -   implement json to make things persist between sessions

public class Jambo extends Application {
    private MediaPlayer mediaPlayer;
    private ListView<String> songListView;
    private List<File> songFiles;
    private Label currentSongLabel;
    private Slider progressSlider;

    private Label timerLabel;
    private Label fileInfoLabel;
    private boolean isDragging = false;

    private boolean shuffleEnabled = false;
    private final Random random = new Random();
    private boolean isMuted = false;

    @Override
    public void start(Stage primaryStage) {
        songListView = new ListView<>();
        songFiles = new ArrayList<>();
        currentSongLabel = new Label("No song playing");

        HBox headerBox = new HBox();
        Button loadButton = new Button("Load Songs");
        loadButton.getStyleClass().add("button");
        loadButton.setOnAction(e -> loadSongs());

        headerBox.getChildren().addAll(loadButton);

        HBox controlButtonBox = new HBox();
        controlButtonBox.setSpacing(10);

        Button playButton = new Button("Play");
        playButton.getStyleClass().add("button");
        playButton.setOnAction(e -> playSelectedSong());

        Button pauseButton = new Button("Pause");
        pauseButton.getStyleClass().add("button");
        pauseButton.setOnAction(e -> pauseMusic());

        Button stopButton = new Button("Stop");
        stopButton.getStyleClass().add("button");
        stopButton.setOnAction(e -> stopMusic());

        Button previousButton = new Button("Previous");
        previousButton.getStyleClass().add("button");
        previousButton.setOnAction(e -> playPreviousSong());

        Button nextButton = new Button("Next");
        nextButton.getStyleClass().add("button");
        nextButton.setOnAction(e -> playNextSong());

        Button shuffleButton = new Button("Shuffle");
        shuffleButton.getStyleClass().add("button");
        shuffleButton.setOnAction(e -> toggleShuffle());

        controlButtonBox.getChildren().addAll(playButton, pauseButton, stopButton, previousButton, nextButton, shuffleButton);

        Slider volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.getStyleClass().add("volume-slider");
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(isMuted ? 0 : newVal.doubleValue());
            }
        });

        Button muteButton = new Button("Mute");
        muteButton.getStyleClass().add("button");
        muteButton.setOnAction(e -> toggleMute());

        HBox volumeControlBox = new HBox(volumeSlider, muteButton);
        volumeControlBox.setSpacing(10); // Space between slider and mute button

        controlButtonBox.getChildren().add(volumeControlBox);

        progressSlider = new Slider(0, 1, 0);
        progressSlider.setValue(0);

        timerLabel = new Label("0:00 / 0:00");
        fileInfoLabel = new Label("Format: - Hz, - kbps");

        progressSlider.setOnMouseDragged(e -> {
            if (mediaPlayer != null) {
                isDragging = true;
            }
        });

        progressSlider.setOnMouseReleased(e -> {
            if (mediaPlayer != null) {
                double newValue = progressSlider.getValue();
                mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(newValue));
                isDragging = false;
            }
        });

        progressSlider.setOnMouseClicked(e -> {
            if (mediaPlayer != null) {
                double clickValue = e.getX() / progressSlider.getWidth();
                mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(clickValue));
                progressSlider.setValue(clickValue);
            }
        });

        HBox progressBox = new HBox(timerLabel, progressSlider, fileInfoLabel);
        progressBox.setSpacing(10);
        HBox.setHgrow(progressSlider, Priority.ALWAYS);

        VBox controlLayout = new VBox(controlButtonBox, new Label(), progressBox);
        controlLayout.setSpacing(10);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(headerBox);
        mainLayout.setCenter(songListView);
        mainLayout.setBottom(controlLayout);

        Scene scene = new Scene(mainLayout, 600, 400);

        URL cssUrl = getClass().getResource("/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("Could not find style.css");
        }

        primaryStage.setTitle("Jambo - 0.2");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(800);
        primaryStage.show();
    }

    private void loadSongs() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            songListView.getItems().clear();
            songFiles.clear();
            File[] files = selectedDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
            if (files != null) {
                for (File file : files) {
                    songFiles.add(file);
                    songListView.getItems().add(file.getName());
                }
            }
        }
    }

    private void playSelectedSong() {
        int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            File songFile = songFiles.get(selectedIndex);
            Media media = new Media(songFile.toURI().toString());

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> {
                currentSongLabel.setText("Playing: " + songFile.getName());
                progressSlider.setMax(1);
                timerLabel.setText(formatTime(mediaPlayer.getTotalDuration().toSeconds(), mediaPlayer.getTotalDuration().toSeconds()));
                updateFileInfoLabel(songFile);
                mediaPlayer.play();
            });

            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (mediaPlayer.getTotalDuration().toSeconds() > 0 && !isDragging) {
                    double progress = newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds();
                    progressSlider.setValue(progress);
                    timerLabel.setText(formatTime(newTime.toSeconds(), mediaPlayer.getTotalDuration().toSeconds()));
                }
            });

            mediaPlayer.setOnEndOfMedia(this::playNextSong);
        } else {
            currentSongLabel.setText("Select a song to play.");
        }
    }

    private void playNextSong() {
        int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            int nextIndex = (selectedIndex + 1) % songFiles.size();
            songListView.getSelectionModel().select(nextIndex);
            playSelectedSong();
        }
    }

    private void playPreviousSong() {
        int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            int previousIndex = (selectedIndex - 1 + songFiles.size()) % songFiles.size();
            songListView.getSelectionModel().select(previousIndex);
            playSelectedSong();
        }
    }

    private void toggleShuffle() {
        shuffleEnabled = !shuffleEnabled;
        if (shuffleEnabled) {
            System.out.println("Shuffle is ON");
        } else {
            System.out.println("Shuffle is OFF");
        }
    }

    private void toggleMute() {
        isMuted = !isMuted;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(isMuted ? 0 : 0.5);
        }
    }

    private void updateFileInfoLabel(File songFile) {

        //TODO - get track metadata

        fileInfoLabel.setText("Format: MP3, 320 kbps");
    }

    private String formatTime(double currentTime, double totalTime) {
        int currentMinutes = (int) (currentTime / 60);
        int currentSeconds = (int) (currentTime % 60);
        int totalMinutes = (int) (totalTime / 60);
        int totalSeconds = (int) (totalTime % 60);
        return String.format("%d:%02d / %d:%02d", currentMinutes, currentSeconds, totalMinutes, totalSeconds);
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();

            //TODO - make it so pressing pause again starts playing the song again

        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            currentSongLabel.setText("No song playing");
            progressSlider.setValue(0);
            timerLabel.setText("0:00 / 0:00");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
