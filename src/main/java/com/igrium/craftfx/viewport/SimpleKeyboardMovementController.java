package com.igrium.craftfx.viewport;

import com.igrium.craftfx.engine.MovementController;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.robot.Robot;
import javafx.stage.Window;
import net.minecraft.client.MinecraftClient;

/**
 * A simple movement controller for the primary viewport. This uses Minecraft's
 * default movement mechanics, making it compatible with servers, but fairly
 * limited in capability.
 */
public class SimpleKeyboardMovementController extends InputController<PrimaryViewport> {

    private boolean pressingForward;
    private boolean pressingBack;
    private boolean pressingLeft;
    private boolean pressingRight;
    private boolean pressingJump;
    private boolean pressingSneak;

    private double prevMouseX;
    private double prevMouseY;

    private boolean isAltHeld;

    // So we don't capture robot moves
    private boolean ignoreMouse;

    protected MovementController controller;
    private boolean isNavigating;
    protected final Robot robot = new Robot();

    private MinecraftClient client;

    public SimpleKeyboardMovementController(PrimaryViewport viewport) {
        super(viewport);
        initGame();
    }

    protected void initGame() {
        client = MinecraftClient.getInstance();
        client.execute(() -> {
            controller = new MovementController(client.options);
            client.player.input = controller;
        });
    }

    @Override
    protected void initListeners(PrimaryViewport viewport) {
        viewport.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (!isNavigating) return;

            if (e.getCode() == KeyCode.W) pressingForward = true;
            else if (e.getCode() == KeyCode.A) pressingLeft = true;
            else if (e.getCode() == KeyCode.S) pressingBack = true;
            else if (e.getCode() == KeyCode.D) pressingRight = true;
            else if (e.getCode() == KeyCode.SHIFT) pressingSneak = true;
            else if (e.getCode() == KeyCode.SPACE) pressingJump = true;
            else return;

            e.consume();
            updateKeyboard();
        });

        viewport.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if (!isNavigating) return;

            if (e.getCode() == KeyCode.W) pressingForward = false;
            else if (e.getCode() == KeyCode.A) pressingLeft = false;
            else if (e.getCode() == KeyCode.S) pressingBack = false;
            else if (e.getCode() == KeyCode.D) pressingRight = false;
            else if (e.getCode() == KeyCode.SHIFT) pressingSneak = false;
            else if (e.getCode() == KeyCode.SPACE) pressingJump = false;
            else return;
            e.consume();
            updateKeyboard();
        });

        viewport.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() != MouseButton.SECONDARY && !isAltHeld) return;
            setNavigating(true);
            e.consume();
        });

        viewport.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (e.getButton() != MouseButton.SECONDARY && !isAltHeld) return;
            setNavigating(false);
            e.consume();
        });

        viewport.addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMouseMove);
        viewport.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseMove);

        viewport.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ALT) isAltHeld = true;
        });

        viewport.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode() == KeyCode.ALT) {
                isAltHeld = false;
                setNavigating(false);
            }
        });
    }

    public boolean isNavigating() {
        return isNavigating;
    }

    public void setNavigating(boolean isNavigating) {
        this.isNavigating = isNavigating;
        if (!isNavigating) {
            pressingLeft = false;
            pressingRight = false;
            pressingForward = false;
            pressingBack = false;
            pressingJump = false;
            pressingSneak = false;
            updateKeyboard();

            ignoreMouse = true;
        }
    }

    protected void handleMouseMove(MouseEvent event) {
        if (!isNavigating) return;

        if (ignoreMouse) {
            ignoreMouse = false;
            prevMouseX = event.getSceneX();
            prevMouseY = event.getSceneY();
            return;
        }

        double dx = event.getSceneX() - prevMouseX;
        double dy = event.getSceneY() - prevMouseY;
        prevMouseX = event.getSceneX();
        prevMouseY = event.getSceneY();

        Window window = getViewport().getScene().getWindow();
        double centerX = window.getX() + window.getWidth() / 2;
        double centerY = window.getY() + window.getHeight() / 2;
        
        ignoreMouse = true;
        robot.mouseMove(centerX, centerY);

        client.player.changeLookDirection(dx, dy);
        event.consume();
    }

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0f;
        }
        return positive ? 1.0f : -1.0f;
    }

    protected void updateKeyboard() {
        controller.setForwardAmount(getMovementMultiplier(pressingForward, pressingBack));
        controller.setSidewaysAmount(getMovementMultiplier(pressingLeft, pressingRight));
        controller.setJumping(pressingJump);
        controller.setSneaking(pressingSneak);
    }

    @Override
    public void close() {
        controller.setIgnoreNative(false);
    }
}
