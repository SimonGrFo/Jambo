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

/**
 * Dialog for managing playlists, allowing users to create, delete, and switch playlists.
 */
public class PlaylistDialog extends Dialog<Void> {

    private final TableView<PlaylistEntry> playlistTable;
    private final ObservableList<PlaylistEntry> playlists;
    private final JamboController controller;

    /**
     * Represents an entry in the playlist table with a name and last modified timestamp.
     */
    public static class PlaylistEntry {
        private final String name;
        private final LocalDateTime lastModified;

        public PlaylistEntry(String name) {
            this.name = name;
            this.lastModified = LocalDateTime.now();
        }

        public String getName() { return name; }

        public LocalDateTime getLastModified() { return lastModified; }

        // Returns formatted date string for display
        public String getFormattedDate() {
            return lastModified.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }

    /**
     * Constructs a PlaylistDialog instance.
     *
     * @param controller The main controller managing playlists.
     */
    public PlaylistDialog(JamboController controller) {
        this.controller = controller;
        DialogInterface dialogService = DependencyContainer.getDialogService();

        setTitle("Playlist Manager");
        setHeaderText("Manage Your Playlists");

        playlistTable = new TableView<>();
        playlists = FXCollections.observableArrayList();

        setupTableColumns();
        setupDialogContent();
        setupDoubleClickHandler();

        initModality(Modality.APPLICATION_MODAL);
        refreshPlaylists();
    }

    /**
     * Sets up the columns for the playlist table, including name, last modified, and action columns.
     */
    private void setupTableColumns() {
        // Playlist name column
        TableColumn<PlaylistEntry, String> nameColumn = new TableColumn<>("Playlist Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        // Last modified date column with formatted date
        TableColumn<PlaylistEntry, String> dateColumn = new TableColumn<>("Last Modified");
        dateColumn.setCellValueFactory(cellData ->
                Bindings.createStringBinding(() -> cellData.getValue().getFormattedDate()));
        dateColumn.setPrefWidth(150);

        // Action column with delete button for each entry
        TableColumn<PlaylistEntry, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setPrefWidth(100);
        actionColumn.setCellFactory(createDeleteButtonCellFactory());

        playlistTable.getColumns().addAll(nameColumn, dateColumn, actionColumn);
        playlistTable.setItems(playlists);
    }

    /**
     * Sets up the dialog's content layout, including buttons and the playlist table.
     */
    private void setupDialogContent() {
        Button newPlaylistButton = new Button("New Playlist");
        newPlaylistButton.setOnAction(event -> createNewPlaylist());

        VBox contentLayout = new VBox(10);
        contentLayout.setPadding(new Insets(10));
        contentLayout.getChildren().addAll(newPlaylistButton, playlistTable);

        getDialogPane().setContent(contentLayout);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }

    /**
     * Handles double-click events on a playlist entry to switch to the selected playlist.
     */
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

    /**
     * Creates a cell factory for generating delete buttons in the action column of each playlist entry.
     */
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


    /**
     * Displays a confirmation dialog for deleting a playlist.
     */
    private boolean showDeleteConfirmationDialog(String playlistName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Playlist");
        alert.setContentText("Are you sure you want to delete the playlist: " + playlistName + "?");
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Prompts the user to create a new playlist and refreshes the table if a valid name is provided.
     */
    private void createNewPlaylist() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Playlist");
        dialog.setHeaderText("Create a new playlist");
        dialog.setContentText("Enter playlist name:");

        dialog.showAndWait().ifPresent(this::attemptAddNewPlaylist);
    }

    /**
     * Validates the playlist name and adds a new playlist if the name is valid.
     */
    private void attemptAddNewPlaylist(String name) {
        if (!name.trim().isEmpty() && isValidPlaylistName(name)) {
            controller.getPlaylistManager().createPlaylist(name);
            refreshPlaylists();
        } else {
            showInvalidNameAlert();
        }
    }

    /**
     * Checks if the given playlist name is valid and doesn't already exist.
     */
    private boolean isValidPlaylistName(String name) {
        return !controller.getPlaylistManager().getPlaylistNames().contains(name) &&
                !name.matches(".*[\\\\/:*?\"<>|].*");
    }

    /**
     * Displays an error alert for an invalid playlist name.
     */
    private void showInvalidNameAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Playlist Name");
        alert.setHeaderText(null);
        alert.setContentText("Playlist name is invalid or already exists. Please choose a different name.");
        alert.showAndWait();
    }

    /**
     * Refreshes the list of playlists by fetching updated data from the controller.
     */
    private void refreshPlaylists() {
        playlists.clear();
        controller.getPlaylistManager().getPlaylistNames()
                .forEach(name -> playlists.add(new PlaylistEntry(name)));
    }
}
