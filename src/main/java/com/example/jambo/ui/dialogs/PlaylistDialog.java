package com.example.jambo.ui.dialogs;

import com.example.jambo.Interfaces.DialogInterface;
import com.example.jambo.controllers.JamboController;
import com.example.jambo.dependency.injection.DependencyContainer;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.util.Callback;
import javafx.beans.binding.Bindings;

public class PlaylistDialog extends Dialog<Void> {

    private final TableView<PlaylistEntry> playlistTable;
    private final ObservableList<PlaylistEntry> playlists;
    private final JamboController controller;

    public static class PlaylistEntry {
        private final String name;
        private final LocalDateTime lastModified;

        public PlaylistEntry(String name) {
            this.name = name;
            this.lastModified = LocalDateTime.now();
        }

        public String getName() { return name; }

        public LocalDateTime getLastModified() { return lastModified; }

        public String getFormattedDate() {
            return lastModified.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }

    public PlaylistDialog(JamboController controller) {
        this.controller = controller;
        DialogInterface dialogService = DependencyContainer.getDialogService();

        setTitle("Playlist Manager");

        playlistTable = new TableView<>();
        playlists = FXCollections.observableArrayList();

        setupTableColumns();
        setupDialogContent();
        setupDoubleClickHandler();

        initModality(Modality.APPLICATION_MODAL);
        refreshPlaylists();
    }

    private void setupTableColumns() {
        TableColumn<PlaylistEntry, String> nameColumn = new TableColumn<>("Playlist Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        TableColumn<PlaylistEntry, String> dateColumn = new TableColumn<>("Last Modified");
        dateColumn.setCellValueFactory(cellData ->
                Bindings.createStringBinding(() -> cellData.getValue().getFormattedDate()));
        dateColumn.setPrefWidth(150);

        TableColumn<PlaylistEntry, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setPrefWidth(100);
        actionColumn.setCellFactory(createDeleteButtonCellFactory());

        playlistTable.getColumns().addAll(nameColumn, dateColumn, actionColumn);
        playlistTable.setItems(playlists);
    }

    private void setupDialogContent() {
        Button newPlaylistButton = new Button("New Playlist");
        newPlaylistButton.setOnAction(event -> createNewPlaylist());

        VBox contentLayout = new VBox(10);
        contentLayout.setPadding(new Insets(10));
        contentLayout.getChildren().addAll(newPlaylistButton, playlistTable);

        getDialogPane().setContent(contentLayout);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }

    private void setupDoubleClickHandler() {
        playlistTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                PlaylistEntry selectedEntry = playlistTable.getSelectionModel().getSelectedItem();
                if (selectedEntry != null) {
                    controller.getPlaylistManager().switchToPlaylist(selectedEntry.getName());
                    this.close();
                }
            }
        });
    }

    private Callback<TableColumn<PlaylistEntry, Void>, TableCell<PlaylistEntry, Void>> createDeleteButtonCellFactory() {
        return column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    PlaylistEntry playlist = getTableView().getItems().get(getIndex());
                    if (!"Default".equals(playlist.getName()) && showDeleteConfirmationDialog(playlist.getName())) {
                        controller.getPlaylistManager().deletePlaylist(playlist.getName());
                        refreshPlaylists();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    PlaylistEntry playlist = getTableView().getItems().get(getIndex());
                    setGraphic("Default".equals(playlist.getName()) ? null : deleteButton);
                }
            }
        };
    }

    private boolean showDeleteConfirmationDialog(String playlistName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Playlist");
        alert.setContentText("Are you sure you want to delete the playlist: " + playlistName + "?");
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void createNewPlaylist() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Playlist");
        dialog.setHeaderText("Create a new playlist");
        dialog.setContentText("Enter playlist name:");

        dialog.showAndWait().ifPresent(this::attemptAddNewPlaylist);
    }

    private void attemptAddNewPlaylist(String name) {
        if (!name.trim().isEmpty() && isValidPlaylistName(name)) {
            controller.getPlaylistManager().createPlaylist(name);
            refreshPlaylists();
        } else {
            showInvalidNameAlert();
        }
    }

    private boolean isValidPlaylistName(String name) {
        return !controller.getPlaylistManager().getPlaylistNames().contains(name) &&
                !name.matches(".*[\\\\/:*?\"<>|].*");
    }

    private void showInvalidNameAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Playlist Name");
        alert.setHeaderText(null);
        alert.setContentText("Playlist name is invalid or already exists. Please choose a different name.");
        alert.showAndWait();
    }

    private void refreshPlaylists() {
        playlists.clear();
        controller.getPlaylistManager().getPlaylistNames()
                .forEach(name -> playlists.add(new PlaylistEntry(name)));
    }
}
