package com.example.jambo.exceptions;

public class MediaPlaybackException extends RuntimeException {
    public MediaPlaybackException(String message, Throwable cause) {
        super(message, cause);
    }
}