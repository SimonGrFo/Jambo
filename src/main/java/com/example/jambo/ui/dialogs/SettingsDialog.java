package com.example.jambo.ui.dialogs;

import com.example.jambo.controllers.JamboController;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.geometry.Insets;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;

public class SettingsDialog extends Dialog<Void> {
    private final JamboController controller;
    private final Preferences preferences;
    private final Map<String, TextField> keybindFields;
    private Button viewLogsButton;
    private Slider crossfadeSlider;

    public SettingsDialog(JamboController controller) {
        this.controller = controller;
        this.preferences = Preferences.userNodeForPackage(SettingsDialog.class);
        this.keybindFields = new HashMap<>();

        setTitle("Settings");

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createGeneralTab(),
                createKeybindsTab(),
                createAudioTab()
        );

        getDialogPane().setContent(tabPane);
        getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.APPLY) {
                try {
                    saveSettings();
                } catch (BackingStoreException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        });

        initModality(Modality.APPLICATION_MODAL);
    }

    private Tab createGeneralTab() {
        Tab tab = new Tab("General");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        viewLogsButton = new Button("View Logs");
        viewLogsButton.setOnAction(e -> showLogsDialog());

        content.getChildren().addAll(
                new Label("Logs:"),
                viewLogsButton
        );

        tab.setContent(content);
        return tab;
    }

    private void showLogsDialog() {
        Dialog<Void> logDialog = new Dialog<>();
        logDialog.setTitle("Console Logs");
        logDialog.setHeaderText("Application Logs");

        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefRowCount(20);
        logArea.setPrefColumnCount(80);

        try {
            File logDir = new File("jambo_logs");
            if (logDir.exists() && logDir.isDirectory()) {
                File[] logFiles = logDir.listFiles((dir, name) -> name.endsWith(".log"));
                if (logFiles != null && logFiles.length > 0) {
                    File mostRecentLog = logFiles[0];
                    for (File file : logFiles) {
                        if (file.lastModified() > mostRecentLog.lastModified()) {
                            mostRecentLog = file;
                        }
                    }
                    String logContent = Files.readString(mostRecentLog.toPath());
                    logArea.setText(logContent);
                } else {
                    logArea.setText("No log files found.");
                }
            } else {
                logArea.setText("Log directory not found.");
            }
        } catch (IOException e) {
            logArea.setText("Error reading log files: " + e.getMessage());
        }

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().add(new ScrollPane(logArea));

        logDialog.getDialogPane().setContent(content);
        logDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        logDialog.initModality(Modality.APPLICATION_MODAL);

        logDialog.showAndWait();
    }

    private Tab createKeybindsTab() {
        Tab tab = new Tab("Keybinds");
        tab.setClosable(false);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        addKeybindSetting(grid, 0, "Play/Pause", "playPauseKey", "SPACE");
        addKeybindSetting(grid, 1, "Next Track", "nextTrackKey", "RIGHT");
        addKeybindSetting(grid, 2, "Previous Track", "previousTrackKey", "LEFT");
        addKeybindSetting(grid, 3, "Volume Up", "volumeUpKey", "UP");
        addKeybindSetting(grid, 4, "Volume Down", "volumeDownKey", "DOWN");
        addKeybindSetting(grid, 5, "Mute", "muteKey", "M");

        tab.setContent(new ScrollPane(grid));
        return tab;
    }

    private Tab createAudioTab() {
        Tab tab = new Tab("Audio");
        tab.setClosable(false);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label crossfadeLabel = new Label("Crossfade Duration (seconds):");
        crossfadeSlider = new Slider(0, 10, preferences.getDouble("crossfade", 0));
        crossfadeSlider.setShowTickLabels(true);
        crossfadeSlider.setShowTickMarks(true);
        crossfadeSlider.setMajorTickUnit(2);

        content.getChildren().addAll(
                crossfadeLabel,
                crossfadeSlider
        );

        tab.setContent(content);
        return tab;
    }

    private void addKeybindSetting(GridPane grid, int row, String label, String prefKey, String defaultValue) {
        grid.add(new Label(label), 0, row);

        TextField keybindField = new TextField(preferences.get(prefKey, defaultValue));
        keybindField.setEditable(false);
        keybindField.setOnKeyPressed(event -> {
            event.consume();
            keybindField.setText(event.getCode().toString());
        });

        keybindFields.put(prefKey, keybindField);
        grid.add(keybindField, 1, row);

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> keybindField.setText(defaultValue));
        grid.add(resetButton, 2, row);
    }

    private void saveSettings() throws BackingStoreException {
        keybindFields.forEach((key, field) ->
                preferences.put(key, field.getText())
        );

        preferences.putDouble("crossfade", crossfadeSlider.getValue());

        preferences.flush();
    }
}