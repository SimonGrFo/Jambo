package com.example.jambo.Interface;

import java.io.File;

public interface IDialogService {
    void showPropertiesDialog(File file);
    String formatFileSize(long bytes);
}