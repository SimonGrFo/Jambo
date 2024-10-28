package com.example.jambo.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IconService {
    private final Map<String, Image> icons = new HashMap<>();

    public IconService() {
        loadIcons();
    }

    private void loadIcons() {
        icons.put("play", loadIcon("/images/icons/play_icon.png"));
        icons.put("pause", loadIcon("/images/icons/pause_icon.png"));
        icons.put("stop", loadIcon("/images/icons/stop_icon.png"));
        icons.put("previous", loadIcon("/images/icons/previous_icon.png"));
        icons.put("next", loadIcon("/images/icons/next_icon.png"));
        icons.put("loop", loadIcon("/images/icons/loop_icon.png"));
        icons.put("shuffle", loadIcon("/images/icons/shuffle_icon.png"));
        icons.put("mute", loadIcon("/images/icons/mute_icon.png"));
    }

    private Image loadIcon(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    public ImageView createIconImageView(String iconName) {
        ImageView imageView = new ImageView(icons.get(iconName));
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        return imageView;
    }
}