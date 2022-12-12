package com.igrium.craftfx.viewport;

import com.igrium.craftfx.engine.MovementHandler;

import javafx.scene.Scene;

/**
 * Handles keystrokes on an engine viewport and translating them to the engine.
 */
public abstract class InputController<T extends EngineViewport, M extends MovementHandler> implements AutoCloseable {
    protected final T viewport;
    protected final M movementHandler;

    public InputController(T viewport, M movementHandler) {
        this.viewport = viewport;
        this.movementHandler = movementHandler;
        initListeners(viewport);
        movementHandler.setController(this);
    }

    /**
     * Initialize JavaFX listeners.
     * @param viewport The viewport to use.
     */
    protected abstract void initListeners(T viewport);

    /**
     * Called every frame on the Minecraft client thread.
     * @param delta The number of milliseconds since the last tick.
     */
    public abstract void tick(long delta);

    public final T getViewport() {
        return viewport;
    }

    public M getMovementHandler() {
        return movementHandler;
    }

    public Scene getScene() {
        return getViewport().getScene();
    }

    @Override
    public void close() {
        movementHandler.setController(null);
    }
}
