package com.example.jambo.ui.dialogs;

import javafx.scene.control.*;
import javafx.stage.Modality;

public class SettingsDialog extends Dialog<Void> {

    public SettingsDialog() {

        setTitle("Settings");

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                createGeneralTab(),
                createKeybindsTab()
        );

        getDialogPane().setContent(tabPane);
        getDialogPane().getButtonTypes().addAll(ButtonType.APPLY); // TODO - make it so apply applies the settings that
                                                                   //        have been modified

        initModality(Modality.APPLICATION_MODAL);
    }

    private Tab createGeneralTab() {
        Tab generalTab = new Tab("Console Log");
        generalTab.setClosable(false);  // TODO - implement
        return generalTab;
    }

    private Tab createKeybindsTab() {
        Tab keybindsTab = new Tab("Keybinds");
        keybindsTab.setClosable(false);  // TODO - implement
        return keybindsTab;
    }
}
