package com.example.jambo.managers;

import com.example.jambo.model.PlayerState;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

@Component
public class PlayerStateManager {
    private final PlayerState state;

    public PlayerStateManager() {
        this.state = new PlayerState(0.5);
    }

    public void applyCurrentState(MediaPlayer player) {
        player.setVolume(state.getVolume());
        player.setCycleCount(state.isLooping() ? MediaPlayer.INDEFINITE : 1);
        if (state.getCurrentPosition() > 0) {
            player.seek(Duration.seconds(state.getCurrentPosition()));
        }
    }

    public PlayerState getCurrentState() {
        return state;
    }
}