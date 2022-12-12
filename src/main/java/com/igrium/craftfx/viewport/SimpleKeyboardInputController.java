package com.igrium.craftfx.viewport;

import com.igrium.craftfx.engine.MovementHandler;
import com.igrium.craftfx.engine.PlayerMovementHandler;

import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.robot.Robot;
import javafx.stage.Window;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;

/**
 * A simple movement controller for the primary viewport. This uses Minecraft's
 * default movement mechanics, making it compatible with servers, but fairly
 * limited in capability.
 */
public class SimpleKeyboardInputController<T extends EngineViewport> extends InputController<T, MovementHandler> {

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

    private boolean isNavigating;
    protected final Robot robot = new Robot();

    private Cursor cursorCache = Cursor.DEFAULT;

    /**
     * Spawn a simple keyboard input controller.
     * @param viewport The viewport to use.
     * @param movementHandler The movement handler to use.
     */
    public SimpleKeyboardInputController(T viewport, MovementHandler movementHandler) {
        super(viewport, movementHandler);
    }

    /**
     * Spawn a simple keyboard input controller using a default player movement handler.
     * @param viewport The viewport to use.
     */
    public SimpleKeyboardInputController(T viewport) {
        this(viewport, setupMovementHandler());
    }
    

    @Override
    protected void initListeners(T viewport) {
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
        if (isNavigating) {
            getScene().setCursor(Cursor.NONE);
        } else {
            pressingLeft = false;
            pressingRight = false;
            pressingForward = false;
            pressingBack = false;
            pressingJump = false;
            pressingSneak = false;
            updateKeyboard();

            ignoreMouse = true;
            getScene().setCursor(cursorCache);
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

        Window window = getScene().getWindow();
        double centerX = window.getX() + window.getWidth() / 2;
        double centerY = window.getY() + window.getHeight() / 2;
        
        ignoreMouse = true;
        robot.mouseMove(centerX, centerY);

        movementHandler.changeLookDirection(dx, dy);
        event.consume();
    }

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0f;
        }
        return positive ? 1.0f : -1.0f;
    }

    protected void updateKeyboard() {
        movementHandler.setForwardAmount(getMovementMultiplier(pressingForward, pressingBack));
        movementHandler.setSidewaysAmount(getMovementMultiplier(pressingLeft, pressingRight));
        movementHandler.setJumping(pressingJump);
        movementHandler.setSneaking(pressingSneak);
    }

    /**
     * Setup a player movement handler for the local player.
     * @return The movement handler.
     * @throws IllegalStateException If the camera entity is not a player.
     */
    public static PlayerMovementHandler setupMovementHandler() throws IllegalStateException {
        MinecraftClient client = MinecraftClient.getInstance();
        Entity camera = client.getCameraEntity();
        if (!(camera instanceof ClientPlayerEntity)) {
            throw new IllegalStateException("Camera entity must be a player!");
        }
        ClientPlayerEntity player = (ClientPlayerEntity) camera;

        if (player.input instanceof PlayerMovementHandler) {
            return (PlayerMovementHandler) player.input;
        }

        PlayerMovementHandler handler = new PlayerMovementHandler(player, client.options);

        client.execute(() -> {
            player.input = handler;
        });

        return handler;
    }

    @Override
    public void tick(long delta) {
        
    }

    @Override
    public void close() {
        
    }
}
