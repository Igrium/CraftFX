package com.igrium.craftfx.viewport;

import com.igrium.craftfx.engine.ArbitraryPlayerMovementHandler;
import com.igrium.craftfx.engine.MovementHandler;

import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.robot.Robot;
import javafx.stage.Window;
import net.minecraft.util.math.Vec3d;

/**
 * An input controller that controls the viewport with standard orbit/pan/zoom
 * controls.
 */
public class StandardInputController<T extends EngineViewport> extends InputController<T, MovementHandler> {

    protected static enum MouseMovementResponse { NONE, CONSUME, ROBOT }

    protected FirstPersonController fpv;

    // So we don't capture robot moves
    private boolean ignoreMouse;

    private boolean isNavigating;
    protected final Robot robot = new Robot();

    private Cursor cursorCache = Cursor.DEFAULT;

    private double prevMouseX;
    private double prevMouseY;

    private boolean isAltHeld;
    

    /**
     * Spawn a standard input controller.
     * 
     * @param viewport        The viewport to use.
     * @param movementHandler The movement handler to use. Must support arbitrary
     *                        movement.
     * @throws IllegalArgumentException If the supplied movement handler doesn't
     *                                  support arbitrary movement.
     */
    public StandardInputController(T viewport, MovementHandler movementHandler) throws IllegalArgumentException {
        super(viewport, movementHandler);
        if (!movementHandler.supportsArbitraryMovement()) {
            throw new IllegalArgumentException("The supplied movement handler must support arbitrary movement.");
        }
        fpv = new FirstPersonController(movementHandler::getPitch, movementHandler::getYaw);
        putKeybinds(Keybinds.DEFAULTS);
    }

    /**
     * Spawn a standard input controller using a default movement handler created
     * from the client's local player entity.
     * 
     * @param viewport The viewport to use.
     */
    public StandardInputController(T viewport) {
        this(viewport, ArbitraryPlayerMovementHandler.createDefault());
    }

    @Override
    protected void initListeners(T viewport) {
        viewport.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ALT) isAltHeld = true;
        });

        viewport.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode() == KeyCode.ALT) {
                isAltHeld = false;
            }
        });

        viewport.addEventFilter(MouseEvent.MOUSE_MOVED, this::handleMouseMove);
        viewport.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseMove);

        initNavigationEvents(viewport);
    }
    

    protected void initNavigationEvents(T viewport) {
        viewport.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (!isNavigating) return;

            KeyCode code = e.getCode();
            if (isKeybind(code, Keybinds.FORWARD)) fpv.pressingForward = true;
            else if (isKeybind(code, Keybinds.BACK)) fpv.pressingBackward = true;
            else if (isKeybind(code, Keybinds.LEFT)) fpv.pressingLeft = true;
            else if (isKeybind(code, Keybinds.RIGHT)) fpv.pressingRight = true;
            else if (isKeybind(code, Keybinds.UP)) fpv.pressingUp = true;
            else if (isKeybind(code, Keybinds.DOWN)) fpv.pressingDown = true;
            else return;

            e.consume();
            fpv.updateKeyboard();
        });

        viewport.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (!isNavigating) return;

            KeyCode code = e.getCode();
            if (isKeybind(code, Keybinds.FORWARD)) fpv.pressingForward = false;
            else if (isKeybind(code, Keybinds.BACK)) fpv.pressingBackward = false;
            else if (isKeybind(code, Keybinds.LEFT)) fpv.pressingLeft = false;
            else if (isKeybind(code, Keybinds.RIGHT)) fpv.pressingRight = false;
            else if (isKeybind(code, Keybinds.UP)) fpv.pressingUp = false;
            else if (isKeybind(code, Keybinds.DOWN)) fpv.pressingDown = false;
            else return;

            e.consume();
            fpv.updateKeyboard();
        });

        viewport.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() != MouseButton.SECONDARY || isAltHeld) return;
            setNavigating(true);
            e.consume();
        });

        viewport.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (!isNavigating || e.getButton() != MouseButton.SECONDARY) return;
            setNavigating(false);
            e.consume();
        });
    }

    public boolean isNavigating() {
        return isNavigating;
    }

    public void setNavigating(boolean isNavigating) {
        if (isNavigating) {
            getScene().setCursor(Cursor.NONE);
        } else {
            fpv.pressingLeft = false;
            fpv.pressingRight = false;
            fpv.pressingForward = false;
            fpv.pressingBackward = false;
            fpv.pressingUp = false;
            fpv.pressingDown = false;
            fpv.updateKeyboard();

            ignoreMouse = true;
            getScene().setCursor(cursorCache);
        }
        this.isNavigating = isNavigating;
    }
    
    private void handleMouseMove(MouseEvent e) {
        if (ignoreMouse) {
            ignoreMouse = false;
            prevMouseX = e.getSceneX();
            prevMouseY = e.getSceneY();
            return;
        }

        double dx = e.getSceneX() - prevMouseX;
        double dy = e.getSceneY() - prevMouseY;
        prevMouseX = e.getSceneX();
        prevMouseY = e.getSceneY();

        MouseMovementResponse response = onMouseMoved(dx, dy);

        if (response == MouseMovementResponse.ROBOT) {
            Window window = getScene().getWindow();
            double centerX = window.getX() + window.getWidth() / 2;
            double centerY = window.getY() + window.getHeight() / 2;

            ignoreMouse = true;
            robot.mouseMove(centerX, centerY);
            e.consume();
        } else if (response == MouseMovementResponse.CONSUME) {
            e.consume();
        }
    };

    protected MouseMovementResponse onMouseMoved(double dx, double dy) {
        if (isNavigating) {
            movementHandler.changeLookDirection(dx, dy);
            return MouseMovementResponse.ROBOT;
        } else {
            return MouseMovementResponse.NONE;
        }
    }

    @Override
    public void tick(long delta) {
        if (isNavigating) {
            Vec3d pos = movementHandler.getPos();
            movementHandler.setPos(fpv.getMovementVector(delta).add(pos));
        }
    }
    
}
