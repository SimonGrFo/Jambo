package com.example.jambo.ui.dialogs;

import com.example.jambo.controllers.JamboController;
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

public class PlaylistDialog extends Dialog<Void> {
    private final TableView<PlaylistEntry> table;
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

        setTitle("Playlist Manager");
        setHeaderText("Manage Your Playlists");

        table = new TableView<>();
        playlists = FXCollections.observableArrayList();

        TableColumn<PlaylistEntry, String> nameColumn = new TableColumn<>("Playlist Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        TableColumn<PlaylistEntry, String> dateColumn = new TableColumn<>("Last Modified");
        dateColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(
                        () -> cellData.getValue().getFormattedDate()
                )
        );
        dateColumn.setPrefWidth(150);

        TableColumn<PlaylistEntry, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setPrefWidth(100);

        actionColumn.setCellFactory(createButtonCellFactory());

        table.getColumns().addAll(nameColumn, dateColumn, actionColumn);
        table.setItems(playlists);

        Button newPlaylistButton = new Button("New Playlist");
        newPlaylistButton.setOnAction(e -> createNewPlaylist());

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().addAll(newPlaylistButton, table);

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        initModality(Modality.APPLICATION_MODAL);

        refreshPlaylists();
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                PlaylistEntry selectedEntry = table.getSelectionModel().getSelectedItem();
                if (selectedEntry != null) {
                    controller.getPlaylistManager().switchToPlaylist(selectedEntry.getName());
                    this.close();
                }
            }
        });
    }

    private Callback<TableColumn<PlaylistEntry, Void>, TableCell<PlaylistEntry, Void>> createButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<PlaylistEntry, Void> call(TableColumn<PlaylistEntry, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setOnAction(event -> {
                            PlaylistEntry playlist = getTableView().getItems().get(getIndex());
                            if (!"Default".equals(playlist.getName()) &&
                                    showConfirmationDialog(playlist.getName())) {
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
        };
    }

    private boolean showConfirmationDialog(String playlistName) {
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

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                controller.getPlaylistManager().createPlaylist(name);
                refreshPlaylists();
            }
        });
    }

    private void refreshPlaylists() {
        playlists.clear();
        for (String playlistName : controller.getPlaylistManager().getPlaylistNames()) {
            playlists.add(new PlaylistEntry(playlistName));
        }
    }
}