package com.example.jambo.ui.dialogs;

import com.example.jambo.controllers.JamboController;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.beans.binding.Bindings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class PlaylistDialog extends Dialog<Void> {

    private static final String DEFAULT_PLAYLIST_NAME = "Default";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private final TableView<PlaylistEntry> playlistTable;
    private final ObservableList<PlaylistEntry> playlists;
    private final JamboController controller;

    public static class PlaylistEntry {
        private final String name;
        private final LocalDateTime creationTime;

        public PlaylistEntry(String name) {
            this.name = name;
            this.creationTime = LocalDateTime.now();
        }

        public String getName() { return name; }

        public String getFormattedDate() {
            return creationTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        }
    }

    public PlaylistDialog(JamboController controller) {
        this.controller = controller;
        this.playlistTable = new TableView<>();
        this.playlists = FXCollections.observableArrayList();

        setTitle("Playlists");
        initModality(Modality.APPLICATION_MODAL);

        setupTableColumns();
        setupDialogContent();
        setupDoubleClickHandler();

        refreshPlaylists();
    }

    private void setupTableColumns() {
        TableColumn<PlaylistEntry, String> nameColumn = new TableColumn<>("Playlist Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        TableColumn<PlaylistEntry, String> dateColumn = new TableColumn<>("Creation Date");
        dateColumn.setCellValueFactory(cellData ->
                Bindings.createStringBinding(() -> cellData.getValue().getFormattedDate()));
        dateColumn.setPrefWidth(150);

        TableColumn<PlaylistEntry, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setPrefWidth(100);
        actionColumn.setCellFactory(createDeleteButtonCellFactory());

        Collections.addAll(playlistTable.getColumns(), nameColumn, dateColumn, actionColumn);
        playlistTable.setItems(playlists);
    }

    private void setupDialogContent() {
        Button newPlaylistButton = new Button("New Playlist");
        newPlaylistButton.setOnAction(event -> showNewPlaylistDialog());

        VBox contentLayout = new VBox(10, newPlaylistButton, playlistTable);
        contentLayout.setPadding(new Insets(10));
        getDialogPane().setContent(contentLayout);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }

    private void setupDoubleClickHandler() {
        playlistTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                PlaylistEntry selectedEntry = playlistTable.getSelectionModel().getSelectedItem();
                if (selectedEntry != null) {
                    switchToPlaylist(selectedEntry.getName());
                }
            }
        });
    }

    private void switchToPlaylist(String playlistName) {
        controller.getPlaylistManager().switchToPlaylist(playlistName);
        controller.updateTitleWithPlaylistName(playlistName);
        this.close();
    }

    private Callback<TableColumn<PlaylistEntry, Void>, TableCell<PlaylistEntry, Void>> createDeleteButtonCellFactory() {
        return column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> handleDelete(getIndex()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || isDefaultPlaylist() ? null : deleteButton);
            }

            private boolean isDefaultPlaylist() {
                PlaylistEntry playlist = getTableView().getItems().get(getIndex());
                return DEFAULT_PLAYLIST_NAME.equals(playlist.getName());
            }
        };
    }

    private void handleDelete(int index) {
        PlaylistEntry playlist = playlists.get(index);
        if (showDeleteConfirmationDialog(playlist.getName())) {
            controller.getPlaylistManager().deletePlaylist(playlist.getName());
            refreshPlaylists();
        }
    }

    private boolean showDeleteConfirmationDialog(String playlistName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Playlist");
        alert.setContentText("Are you sure you want to delete the playlist: " + playlistName + "?");
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showNewPlaylistDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Playlist");
        dialog.setHeaderText("Create a new playlist");
        dialog.setContentText("Enter playlist name:");

        dialog.showAndWait().ifPresent(this::attemptAddNewPlaylist);
    }

    private void attemptAddNewPlaylist(String name) {
        if (!name.trim().isEmpty() && isUniquePlaylistName(name)) {
            controller.getPlaylistManager().createPlaylist(name);
            refreshPlaylists();
        }
    }

    private boolean isUniquePlaylistName(String name) {
        return playlists.stream().noneMatch(entry -> entry.getName().equalsIgnoreCase(name));
    }

    private void refreshPlaylists() {
        playlists.clear();
        controller.getPlaylistManager().getPlaylistNames()
                .forEach(name -> playlists.add(new PlaylistEntry(name)));
    }
}