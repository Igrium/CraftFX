package com.igrium.craftfx.viewport;

import org.apache.commons.lang3.tuple.Triple;

import com.igrium.craftfx.engine.ArbitraryPlayerMovementHandler;
import com.igrium.craftfx.engine.MovementHandler;
import com.igrium.craftfx.util.RaycastUtils;
import com.igrium.craftfx.viewport.movement.FirstPersonController;
import com.igrium.craftfx.viewport.movement.MouseMovementController;
import com.igrium.craftfx.viewport.movement.OrbitController;

import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.robot.Robot;
import javafx.stage.Window;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

/**
 * An input controller that controls the viewport with standard orbit/pan/zoom
 * controls.
 */
public class StandardInputController<T extends EngineViewport> extends InputController<T, MovementHandler> {

    protected static enum MouseMovementResponse { NONE, CONSUME, ROBOT }

    protected FirstPersonController fpv;
    protected MouseMovementController orbit;

    // So we don't capture robot moves
    private boolean ignoreMouse;

    private boolean isNavigating;
    private boolean isOrbiting;

    protected final Robot robot = new Robot();

    private Cursor cursorCache = Cursor.DEFAULT;

    private double prevMouseX;
    private double prevMouseY;    

    protected boolean autoUpdateFocusPoint;

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
        orbit = new OrbitController(movementHandler::getPos, movementHandler::getPitch, movementHandler::getYaw);
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

        viewport.addEventFilter(MouseEvent.MOUSE_MOVED, this::handleMouseMove);
        viewport.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseMove);

        initMovementEvents(viewport);
        initNavigationEvents(viewport);
    }
    
    protected void initMovementEvents(T viewport) {
        viewport.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.isAltDown()) {
                if (autoUpdateFocusPoint) updateFocusPoint(e.getX(), e.getY());
                setOrbiting(true);
                e.consume();

            }
        });

        viewport.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (e.getButton() == MouseButton.PRIMARY && isOrbiting()) {
                setOrbiting(false);
                e.consume();
            }
        });
    }

    public final boolean autoUpdateFocusPoint() {
        return autoUpdateFocusPoint;
    }

    public void setAutoUpdateFocusPoint(boolean autoUpdateFocusPoint) {
        this.autoUpdateFocusPoint = autoUpdateFocusPoint;
    }

    private void updateFocusPoint(double x, double y) {
        HitResult result = RaycastUtils.raycastViewport((float) x, (float) y, (float) viewport.getWidth(),
                (float) viewport.getHeight(), 1000, ent -> true, false);
        if (result.getType() == HitResult.Type.MISS) return;
        setFocusPoint(result.getPos());
    }

    /**
     * Set the focus point for this controller.
     * @param focusPoint The new focus point.
     */
    public void setFocusPoint(Vec3d focusPoint) {
        orbit.setFocusPoint(focusPoint);
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
            if (e.getButton() != MouseButton.SECONDARY || e.isAltDown()) return;
            setNavigating(true);
            e.consume();
        });

        viewport.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (!isNavigating || e.getButton() != MouseButton.SECONDARY) return;
            setNavigating(false);
            e.consume();
        });
    }

    public final boolean isNavigating() {
        return isNavigating;
    }

    public void setNavigating(boolean isNavigating) {
        if (isNavigating) {
            getScene().setCursor(Cursor.NONE);
            setOrbiting(false);
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

    public final boolean isOrbiting() {
        return isOrbiting;
    }

    public void setOrbiting(boolean isOrbiting) {
        if (isOrbiting) {
            setNavigating(false);
        }
        this.isOrbiting = isOrbiting;
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

        MouseMovementResponse response = onMouseMoved(e, dx, dy);

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

    protected MouseMovementResponse onMouseMoved(MouseEvent e, double dx, double dy) {
        if (isNavigating) {
            movementHandler.changeLookDirection(dx, dy);
            return MouseMovementResponse.ROBOT;
        } else if (isOrbiting) {
            applyMouseMovementController(orbit, dx, dy);
            return MouseMovementResponse.CONSUME;
        } else {
            return MouseMovementResponse.NONE;
        }
    }

    private void applyMouseMovementController(MouseMovementController controller, double dx, double dy) {
        Triple<Vec3d, Float, Float> transform = controller.update(dx, dy);
        movementHandler.setPos(transform.getLeft());
        movementHandler.setPitch(transform.getMiddle());
        movementHandler.setYaw(transform.getRight());
    }

    @Override
    public void tick(long delta) {
        if (isNavigating) {
            Vec3d pos = movementHandler.getPos();
            movementHandler.setPos(fpv.getMovementVector(delta).add(pos));
        }
    }
    
}
