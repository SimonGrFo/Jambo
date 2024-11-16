package com.example.jambo.exceptions;

public class CommandExecutionException extends RuntimeException {
    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}