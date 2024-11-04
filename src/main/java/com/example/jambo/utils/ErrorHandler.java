package com.example.jambo.utils;

import com.example.jambo.exceptions.AudioPlayerException;
import com.example.jambo.services.LoggingService;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ErrorHandler {
    public static void handleException(AudioPlayerException e) {
        LoggingService.logError(e.getMessage(), e);
        showErrorDialog(e.getErrorType().getDescription(), e.getMessage(), e);
    }

    public static void handleGenericException(String context, Throwable e) {
        LoggingService.logError(context + ": " + e.getMessage(), e);
        showErrorDialog("Error", context + ": " + e.getMessage(), e);
    }

    private static void showErrorDialog(String header, String content, Throwable e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(header);
            alert.setContentText(content);

            if (e != null) {
                StringBuffer sb = new StringBuffer();
                sb.append("Exception Stack Trace:\n\n");
                for (StackTraceElement element : e.getStackTrace()) {
                    sb.append(element.toString()).append("\n");
                }

                TextArea textArea = new TextArea(sb.toString());
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);

                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(textArea, 0, 0);

                alert.getDialogPane().setExpandableContent(expContent);
            }

            alert.showAndWait();
        });
    }
}
