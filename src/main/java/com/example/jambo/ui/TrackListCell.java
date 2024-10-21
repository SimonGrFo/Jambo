package com.example.jambo.ui;

import com.example.jambo.model.Track;
import javafx.scene.control.ListCell;

public class TrackListCell extends ListCell<Track> {
    @Override
    protected void updateItem(Track track, boolean empty) {
        super.updateItem(track, empty);
        if (empty || track == null) {
            setText(null);
        } else {
            setText(track.name());
        }
    }
}
