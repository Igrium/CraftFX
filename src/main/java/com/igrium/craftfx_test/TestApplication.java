package com.igrium.craftfx_test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igrium.craftfx.application.CraftApplication;
import com.igrium.craftfx.engine.MovementHandler;
import com.igrium.craftfx.util.RaycastUtils;
import com.igrium.craftfx.viewport.PrimaryViewport;
import com.igrium.craftfx.viewport.SimpleKeyboardMovementController;
import com.igrium.craftfx.application.ApplicationType;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class TestApplication extends CraftApplication {

    private final Logger LOGGER = LogManager.getLogger();
    private PrimaryViewport viewport;
    private SimpleKeyboardMovementController<PrimaryViewport> controller;

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

        viewport = new PrimaryViewport();
        root.getChildren().addAll(viewport);
                
        MovementHandler movementHandler = SimpleKeyboardMovementController.setupMovementHandler();
        controller = new SimpleKeyboardMovementController<>(viewport, movementHandler);

        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }

    protected void onClick(MouseEvent e) {
        IntegratedServer server = MinecraftClient.getInstance().getServer();
        if (server == null) return;

        HitResult hit = RaycastUtils.raycastViewport(
                (float) e.getX(),
                (float) e.getY(),
                (float) viewport.getWidth(),
                (float) viewport.getHeight(),
                10000,
                ent -> true,
                false);
        
        if (hit.getType() == HitResult.Type.BLOCK) {
            Vec3d pos = hit.getPos();
            server.execute(() -> {
                TntEntity entity = new TntEntity(server.getOverworld(), pos.x, pos.y, pos.z, null);
                server.getOverworld().spawnEntity(entity);
            });
        }
    }


    @Override
    public void onClosed() {
        viewport.close();
        controller.close();
    }
    
}
