package com.igrium.craftfx.viewport;

import com.igrium.craftfx.engine.MovementController;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.minecraft.client.MinecraftClient;

/**
 * A simple movement controller for the primary viewport.
 */
public class SimpleMovementController extends InputController<PrimaryViewport> {

    private boolean pressingForward;
    private boolean pressingBack;
    private boolean pressingLeft;
    private boolean pressingRight;
    private boolean pressingJump;
    private boolean pressingSneak;

    protected MovementController controller;

    public SimpleMovementController(PrimaryViewport viewport) {
        super(viewport);
        initGame();
    }

    private void initGame() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            controller = new MovementController(client.options);
            client.player.input = controller;
        });
    }

    @Override
    protected void initListeners(PrimaryViewport viewport) {
        viewport.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.W) pressingForward = true;
            else if (e.getCode() == KeyCode.A) pressingLeft = true;
            else if (e.getCode() == KeyCode.S) pressingBack = true;
            else if (e.getCode() == KeyCode.D) pressingRight = true;
            else if (e.getCode() == KeyCode.SHIFT) pressingSneak = true;
            else if (e.getCode() == KeyCode.SPACE) pressingJump = true;

            e.consume();
            update();
        });

        viewport.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode() == KeyCode.W) pressingForward = false;
            else if (e.getCode() == KeyCode.A) pressingLeft = false;
            else if (e.getCode() == KeyCode.S) pressingBack = false;
            else if (e.getCode() == KeyCode.D) pressingRight = false;
            else if (e.getCode() == KeyCode.SHIFT) pressingSneak = false;
            else if (e.getCode() == KeyCode.SPACE) pressingJump = false;

            e.consume();
            update();
        });
    }

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0f;
        }
        return positive ? 1.0f : -1.0f;
    }

    protected void update() {
        controller.setForwardAmount(getMovementMultiplier(pressingForward, pressingBack));
        controller.setSidewaysAmount(getMovementMultiplier(pressingRight, pressingLeft));
        controller.setJumping(pressingJump);
        controller.setSneaking(pressingSneak);
    }

    @Override
    public void close() {
        controller.setIgnoreNative(false);
    }
}
