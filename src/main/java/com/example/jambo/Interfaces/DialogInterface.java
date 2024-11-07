package com.example.jambo.Interfaces;
import java.io.File;

public interface DialogInterface {
    void showPropertiesDialog(File file);
    String formatFileSize(long bytes);
}