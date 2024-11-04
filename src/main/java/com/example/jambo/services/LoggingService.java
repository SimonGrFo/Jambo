package com.example.jambo.services;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class LoggingService {
    private static final Logger LOGGER = Logger.getLogger(LoggingService.class.getName());
    private static final String LOG_FILE_PATH = "jambo_logs";
    private static boolean isInitialized = false;

    public static void initialize() {
        if (isInitialized) {
            return;
        }

        try {
            File logDir = new File(LOG_FILE_PATH);
            if (!logDir.exists()) {
                logDir.mkdir();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String logFile = LOG_FILE_PATH + File.separator + "jambo_" + timestamp + ".log";

            FileHandler fileHandler = new FileHandler(logFile, true);
            fileHandler.setFormatter(new CustomLogFormatter());
            LOGGER.addHandler(fileHandler);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new CustomLogFormatter());
            LOGGER.addHandler(consoleHandler);

            LOGGER.setLevel(Level.ALL);
            isInitialized = true;

            LOGGER.info("Logging system initialized successfully");
        } catch (IOException e) {
            System.err.println("Failed to initialize logging system: " + e.getMessage());
        }
    }

    private static class CustomLogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            builder.append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .append(" [")
                    .append(record.getLevel())
                    .append("] ")
                    .append(record.getSourceClassName())
                    .append(".")
                    .append(record.getSourceMethodName())
                    .append(": ")
                    .append(record.getMessage())
                    .append("\n");

            if (record.getThrown() != null) {
                builder.append("Exception details:\n");
                for (Throwable throwable : record.getThrown().getSuppressed()) {
                    builder.append(throwable.toString()).append("\n");
                    for (StackTraceElement element : throwable.getStackTrace()) {
                        builder.append("\tat ").append(element.toString()).append("\n");
                    }
                }
            }

            return builder.toString();
        }
    }

    public static void logError(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, message, throwable);
    }

    public static void logWarning(String message) {
        LOGGER.warning(message);
    }

    public static void logInfo(String message) {
        LOGGER.info(message);
    }

    public static void logDebug(String message) {
        LOGGER.fine(message);
    }
}
