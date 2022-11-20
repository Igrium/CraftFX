package com.igrium.craftfx_test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igrium.craftfx.application.CraftApplication;
import com.igrium.craftfx.viewport.PrimaryViewport;
import com.igrium.craftfx.application.ApplicationType;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.minecraft.client.MinecraftClient;

public class TestApplication extends CraftApplication {

    private final Logger LOGGER = LogManager.getLogger();

    public TestApplication(ApplicationType<?> type, MinecraftClient client) {
        super(type, client);
    }

    @Override
    public void start(Stage primaryStage, Application parent) throws Exception {
        primaryStage.setTitle("Hello world!");
        Button btn = new Button();
        btn.setText("Hello World!");
        btn.setOnAction(e -> {
            LOGGER.info("Hello World!");
        });

        StackPane root = new StackPane();

        PrimaryViewport viewport = new PrimaryViewport();
        root.getChildren().addAll(viewport);

        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.show();
    }
    
}
