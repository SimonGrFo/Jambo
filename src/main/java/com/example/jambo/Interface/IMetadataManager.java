package com.example.jambo.Interface;

import java.io.File;

public interface IMetadataManager {
    String formatSongMetadata(File file) throws Exception;
    void updateFileInfo(File songFile);
}