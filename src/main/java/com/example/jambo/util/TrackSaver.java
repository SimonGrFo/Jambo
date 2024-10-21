package com.example.jambo.util;

import com.example.jambo.model.Track;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TrackSaver {

    private final String tracksFile;

    public TrackSaver(String tracksFile) {
        this.tracksFile = tracksFile;
    }

    public void saveTracks(List<Track> tracks) {
        JSONArray jsonTracks = new JSONArray();
        for (Track track : tracks) {
            jsonTracks.put(track.path());
        }
        try {
            Files.write(Paths.get(tracksFile), jsonTracks.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Track> loadTracks() {
        List<Track> loadedTracks = new ArrayList<>();
        try {
            if (Files.exists(Paths.get(tracksFile))) {
                String content = new String(Files.readAllBytes(Paths.get(tracksFile)));
                JSONArray jsonTracks = new JSONArray(content);

                for (int i = 0; i < jsonTracks.length(); i++) {
                    String path = jsonTracks.getString(i);
                    File file = new File(path);
                    if (file.exists()) {
                        loadedTracks.add(new Track(file.getName(), path));
                    } else {
                        System.out.println("File does not exist: " + path);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedTracks;
    }
}
