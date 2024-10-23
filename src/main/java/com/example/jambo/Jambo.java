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
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileWriter;
import java.io.FileReader;
import java.lang.reflect.Type;


public class Jambo extends Application {
    private MediaPlayer mediaPlayer;
    private ListView<String> songListView;
    private List<File> songFiles;
    private List<File> loadedDirectories = new ArrayList<>();
    private Label currentSongLabel;
    private Slider progressSlider;

    private Label timerLabel;
    private Label fileInfoLabel;
    private boolean isDragging = false;

    private boolean shuffleEnabled = false;
    private final Random random = new Random();
    private boolean isMuted = false;
    private Slider volumeSlider;

    private ImageView albumArtView;

    Image playIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/play_icon.png")));
    Image pauseIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/pause_icon.png")));
    Image stopIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/stop_icon.png")));
    Image previousIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/previous_icon.png")));
    Image nextIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/next_icon.png")));
    Image loopIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/loop_icon.png")));
    Image shuffleIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/shuffle_icon.png")));
    Image muteIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/mute_icon.png")));

    private ImageView createIconImageView(Image icon) {
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        return imageView;
    }

    ImageView playImageView = createIconImageView(playIcon);
    ImageView pauseImageView = createIconImageView(pauseIcon);
    ImageView stopImageView = createIconImageView(stopIcon);
    ImageView previousImageView = createIconImageView(previousIcon);
    ImageView nextImageView = createIconImageView(nextIcon);
    ImageView loopImageView = createIconImageView(loopIcon);
    ImageView shuffleImageView = createIconImageView(shuffleIcon);
    ImageView muteIconView = createIconImageView(muteIcon);

    @Override
    public void start(Stage primaryStage) {
        songListView = new ListView<>();
        songFiles = new ArrayList<>();
        loadedDirectories = new ArrayList<>();
        currentSongLabel = new Label("No song playing");

        albumArtView = new ImageView();
        albumArtView.setFitWidth(100);
        albumArtView.setFitHeight(100);
        albumArtView.setPreserveRatio(true);

        loadDefaultAlbumArt();
        loadSongsFromJson();

        HBox headerBox = new HBox();
        Button loadButton = new Button("Load Songs");
        loadButton.getStyleClass().add("button");
        loadButton.setOnAction(e -> loadSongs());

        Button clearButton = new Button("Clear Songs");
        clearButton.getStyleClass().add("button");
        clearButton.setOnAction(e -> clearAllSongs());

        headerBox.getChildren().addAll(loadButton, clearButton);

        HBox controlButtonBox = new HBox();
        controlButtonBox.setSpacing(10);

        Button playButton = new Button("", playImageView);
        playButton.getStyleClass().add("button");
        playButton.setOnAction(e -> playSelectedSong());

        Button pauseButton = new Button("", pauseImageView);
        pauseButton.getStyleClass().add("button");
        pauseButton.setOnAction(e -> pauseMusic());

        Button stopButton = new Button("", stopImageView);
        stopButton.getStyleClass().add("button");
        stopButton.setOnAction(e -> stopMusic());

        Button previousButton = new Button("", previousImageView);
        previousButton.getStyleClass().add("button");
        previousButton.setOnAction(e -> playPreviousSong());

        Button nextButton = new Button("", nextImageView);
        nextButton.getStyleClass().add("button");
        nextButton.setOnAction(e -> playNextSong());

        Button loopButton = new Button("", loopImageView);
        loopButton.getStyleClass().add("button");
        loopButton.setOnAction(e -> toggleLoop());

        Button shuffleButton = new Button("", shuffleImageView);
        shuffleButton.getStyleClass().add("button");
        shuffleButton.setOnAction(e -> toggleShuffle());


        controlButtonBox.getChildren().addAll(playButton, pauseButton, stopButton, previousButton, nextButton, shuffleButton, loopButton);

        volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.getStyleClass().add("volume-slider");
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(isMuted ? 0 : newVal.doubleValue());
            }
        });

        Button muteButton = new Button("", muteIconView);
        muteButton.getStyleClass().add("button");
        muteButton.setOnAction(e -> toggleMute());

        HBox volumeControlBox = new HBox(volumeSlider, muteButton);
        volumeControlBox.setSpacing(10);
        controlButtonBox.getChildren().add(volumeControlBox);

        HBox controlBoxWithArt = new HBox(albumArtView, controlButtonBox);
        controlBoxWithArt.setSpacing(20);

        controlButtonBox.setSpacing(10);
        HBox.setHgrow(controlButtonBox, Priority.ALWAYS);

        progressSlider = new Slider(0, 1, 0);
        progressSlider.setValue(0);
        timerLabel = new Label("0:00 / 0:00");

        fileInfoLabel = new Label("Format: - Hz, - kbps");
        fileInfoLabel.getStyleClass().add("file-info-label");

        progressSlider = new Slider(0, 1, 0);
        progressSlider.setValue(0);

        timerLabel = new Label("0:00 / 0:00");

        HBox progressBox = new HBox(timerLabel, progressSlider);
        progressBox.setSpacing(10);
        HBox.setHgrow(progressSlider, Priority.ALWAYS);

        VBox progressContainer = new VBox(progressBox, fileInfoLabel);
        progressContainer.setSpacing(5);

        HBox.setHgrow(progressSlider, Priority.ALWAYS);

        VBox controlLayout = new VBox(controlBoxWithArt, progressContainer);
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

        primaryStage.setOnCloseRequest(event -> saveSongsToJson(songFiles));

        songListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                playSelectedSong();
            }
        });

        progressSlider.setOnMousePressed(e -> isDragging = true);

        progressSlider.setOnMouseReleased(e -> {
            if (mediaPlayer != null && isDragging) {
                double newTime = progressSlider.getValue() * mediaPlayer.getTotalDuration().toSeconds();
                mediaPlayer.seek(javafx.util.Duration.seconds(newTime));
            }
            isDragging = false;
        });
    }

    private void clearAllSongs() {
        songFiles.clear();
        songListView.getItems().clear();
        currentSongLabel.setText("No song playing");
        progressSlider.setValue(0);
        timerLabel.setText("0:00 / 0:00");
        albumArtView.setImage(null);
        System.out.println("All songs cleared.");
    }

    private void loadSongsFromJson() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("saved_songs.json")) {
            Type listType = new TypeToken<ArrayList<String>>(){}.getType();
            List<String> songPaths = gson.fromJson(reader, listType);
            songFiles.clear();

            for (String path : songPaths) {
                File songFile = new File(path);
                if (songFile.exists()) {
                    songFiles.add(songFile);
                    String formattedSongInfo = formatSongMetadata(songFile);
                    songListView.getItems().add(formattedSongInfo);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading songs from JSON: " + e.getMessage());
        }
    }


    private String formatSongMetadata(File file) throws Exception {
        AudioFile audioFile = AudioFileIO.read(file);
        AudioHeader audioHeader = audioFile.getAudioHeader();
        Tag tag = audioFile.getTag();

        String artist = tag.getFirst(org.jaudiotagger.tag.FieldKey.ARTIST);
        String album = tag.getFirst(org.jaudiotagger.tag.FieldKey.ALBUM);
        String title = tag.getFirst(org.jaudiotagger.tag.FieldKey.TITLE);
        int durationInSeconds = audioHeader.getTrackLength();
        String duration = formatTime(durationInSeconds);

        if (artist == null || artist.isEmpty()) artist = "Unknown Artist";
        if (album == null || album.isEmpty()) album = "Unknown Album";
        if (title == null || title.isEmpty()) title = file.getName();

        return String.format("%s - %s - %s (%s)", artist, album, title, duration);
    }

    private void loadSongs() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            if (!loadedDirectories.contains(selectedDirectory)) {
                loadedDirectories.add(selectedDirectory);
            }

            File[] files = selectedDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
            if (files != null) {
                for (File file : files) {
                    if (!songFiles.contains(file)) {
                        try {
                            String formattedSongInfo = formatSongMetadata(file);
                            songFiles.add(file);
                            songListView.getItems().add(formattedSongInfo);
                        } catch (Exception e) {
                            System.err.println("Error reading metadata: " + e.getMessage());
                            songFiles.add(file);
                            songListView.getItems().add(file.getName());
                        }
                    }
                }
                saveSongsToJson(songFiles);
            }
        }
    }

    private void saveSongsToJson(List<File> songFiles) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("saved_songs.json")) {
            List<String> songPaths = new ArrayList<>();
            for (File file : songFiles) {
                songPaths.add(file.getAbsolutePath());
            }
            gson.toJson(songPaths, writer);
        } catch (Exception e) {
            System.err.println("Error saving songs: " + e.getMessage());
        }
    }



    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    private void playSelectedSong() {
        int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            File songFile = songFiles.get(selectedIndex);
            Media media = new Media(songFile.toURI().toString());

            double currentVolume = volumeSlider.getValue();

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnError(() -> System.err.println("Media player error: " + mediaPlayer.getError().getMessage()));

            mediaPlayer.setOnReady(() -> {
                currentSongLabel.setText("Playing: " + songFile.getName());
                progressSlider.setMax(1);
                timerLabel.setText(formatTime(mediaPlayer.getTotalDuration().toSeconds(), mediaPlayer.getTotalDuration().toSeconds()));
                mediaPlayer.setVolume(isMuted ? 0 : currentVolume);
                updateFileInfoLabel(songFile);
                mediaPlayer.setCycleCount(isLooping ? MediaPlayer.INDEFINITE : 1);
                mediaPlayer.play();
            });

            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (mediaPlayer.getTotalDuration().toSeconds() > 0 && !isDragging) {
                    double progress = newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds();
                    progressSlider.setValue(progress);
                    timerLabel.setText(formatTime(newTime.toSeconds(), mediaPlayer.getTotalDuration().toSeconds()));
                }
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                if (!isLooping) {
                    playNextSong();
                }
            });
        } else {
            currentSongLabel.setText("Select a song to play.");
        }
    }

    private void playNextSong() {
        int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            int nextIndex;
            if (shuffleEnabled) {
                nextIndex = random.nextInt(songFiles.size());
            } else {
                nextIndex = (selectedIndex + 1) % songFiles.size();
            }
            songListView.getSelectionModel().select(nextIndex);
            playSelectedSong();
        } else {
            currentSongLabel.setText("No more songs in the list.");
        }
    }

    private void playPreviousSong() {
        int selectedIndex = songListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            int previousIndex;
            if (shuffleEnabled) {
                previousIndex = random.nextInt(songFiles.size());
            } else {
                previousIndex = (selectedIndex - 1 + songFiles.size()) % songFiles.size();
            }
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

    private boolean isLooping = false;

    private void toggleLoop() {
        isLooping = !isLooping;
        if (mediaPlayer != null) {
            mediaPlayer.setCycleCount(isLooping ? MediaPlayer.INDEFINITE : 1);
        }
        System.out.println("Loop is " + (isLooping ? "ON" : "OFF"));
    }

    private void toggleMute() {
        isMuted = !isMuted;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(isMuted ? 0 : 0.5);
        }
    }

    private void updateFileInfoLabel(File songFile) {
        try {
            AudioFile audioFile = AudioFileIO.read(songFile);
            AudioHeader audioHeader = audioFile.getAudioHeader();

            String format = audioHeader.getFormat();
            String bitrate = audioHeader.getBitRate();
            String sampleRate = audioHeader.getSampleRate();

            Tag tag = audioFile.getTag();
            String artist = tag.getFirst(org.jaudiotagger.tag.FieldKey.ARTIST);
            String album = tag.getFirst(org.jaudiotagger.tag.FieldKey.ALBUM);
            String title = tag.getFirst(org.jaudiotagger.tag.FieldKey.TITLE);

            fileInfoLabel.setText(String.format("%s, %s kbps, %s Hz", format, bitrate, sampleRate));

            currentSongLabel.setText(String.format("Playing: %s - %s - %s", artist, album, title));

            Artwork artwork = tag.getFirstArtwork();
            if (artwork != null) {
                byte[] imageData = artwork.getBinaryData();
                if (imageData != null && imageData.length > 0) {
                    Image albumArtImage = new Image(new ByteArrayInputStream(imageData));
                    albumArtView.setImage(albumArtImage);
                } else {
                    loadDefaultAlbumArt();
                }
            } else {
                loadDefaultAlbumArt();
            }
        } catch (Exception e) {
            fileInfoLabel.setText("Error retrieving metadata");
            System.err.println("Error reading metadata: " + e.getMessage());
            loadDefaultAlbumArt();
        }
    }

    private void loadDefaultAlbumArt() {
        try {
            Image defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/default_album_art.png")));
            albumArtView.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Error loading default album art: " + e.getMessage());
        }
    }

    private String formatTime(double currentTime, double totalTime) {
        int currentMinutes = (int) (currentTime / 60);
        int currentSeconds = (int) (currentTime % 60);
        int totalMinutes = (int) (totalTime / 60);
        int totalSeconds = (int) (totalTime % 60);
        return String.format("%d:%02d / %d:%02d", currentMinutes, currentSeconds, totalMinutes, totalSeconds);
    }

    private boolean isPaused = false;

    private void pauseMusic() {
        if (mediaPlayer != null) {
            if (isPaused) {
                mediaPlayer.play();
                isPaused = false;
            } else {
                mediaPlayer.pause();
                isPaused = true;
            }
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
