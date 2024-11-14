package com.example.jambo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LoggerUtil {
    public static String formatFileInfo(File file) {
        if (file == null) return "null";
        return String.format("'%s' (size: %d bytes)", file.getName(), file.length());
    }

    public static String formatException(Exception e) {
        return String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage());
    }

    public static void logMediaPlayerState(Logger logger, String state, String details) {
        logger.debug("MediaPlayer State Change - {} | Details: {}", state, details);
    }
}
