package com.example.jambo.exceptions;

public class AudioPlayerException extends Exception {
    private final ErrorType errorType;
    private final String contextInfo;  // Added for better error context

    public enum ErrorType {
        FILE_NOT_FOUND("Audio file not found"),
        INVALID_FORMAT("Invalid audio format"),
        PLAYBACK_ERROR("Error during playback"),
        METADATA_ERROR("Error reading metadata"),
        PLAYLIST_ERROR("Error managing playlist"),
        INITIALIZATION_ERROR("Error initializing component");

        private final String description;

        ErrorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public AudioPlayerException(ErrorType errorType, String message) {
        this(errorType, message, null, null);
    }

    public AudioPlayerException(ErrorType errorType, String message, Throwable cause) {
        this(errorType, message, cause, null);
    }

    public AudioPlayerException(ErrorType errorType, String message, String contextInfo) {
        this(errorType, message, null, contextInfo);
    }

    public AudioPlayerException(ErrorType errorType, String message, Throwable cause, String contextInfo) {
        super(message, cause);
        this.errorType = errorType;
        this.contextInfo = contextInfo;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public String getContextInfo() {
        return contextInfo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append(getClass().getName())
                .append(": [")
                .append(errorType)
                .append("] ")
                .append(getMessage());

        if (contextInfo != null) {
            sb.append(" (Context: ").append(contextInfo).append(")");
        }
        return sb.toString();
    }
}