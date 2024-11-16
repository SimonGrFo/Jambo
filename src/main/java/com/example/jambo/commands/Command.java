package com.example.jambo.commands;

public interface Command {
    void execute();
    void undo();
}