package com.example.jambo;

import com.example.jambo.config.JamboConfig;
import com.example.jambo.controllers.JamboController;
import com.example.jambo.ui.JamboUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;

public class Jambo extends Application {
    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(JamboConfig.class).run();
    }

    @Override
    public void start(Stage primaryStage) {
        JamboUI ui = springContext.getBean(JamboUI.class);
        JamboController controller = springContext.getBean(JamboController.class);

        controller.initializeStage(primaryStage);
        Scene scene = ui.createScene(controller);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
