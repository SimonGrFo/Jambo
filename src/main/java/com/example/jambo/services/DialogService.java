package com.example.jambo.services;

import com.example.jambo.Interfaces.DialogInterface;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.io.File;
import java.util.Date;

public class DialogService implements DialogInterface {
    @Override
    public void showPropertiesDialog(File file) {
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

    @Override
    public String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}