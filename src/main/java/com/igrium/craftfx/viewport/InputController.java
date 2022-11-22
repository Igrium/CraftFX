package com.igrium.craftfx.viewport;

import java.io.Closeable;

import com.igrium.craftfx.engine.MovementHandler;

import javafx.scene.Scene;

/**
 * Handles keystrokes on an engine viewport and translating them to the engine.
 */
public abstract class InputController<T extends EngineViewport, M extends MovementHandler> implements Closeable {
    protected final T viewport;
    protected final M movementHandler;

    public InputController(T viewport, M movementHandler) {
        this.viewport = viewport;
        this.movementHandler = movementHandler;
        initListeners(viewport);
    }

    protected abstract void initListeners(T viewport);

    public final T getViewport() {
        return viewport;
    }

    public M getMovementHandler() {
        return movementHandler;
    }

    public Scene getScene() {
        return getViewport().getScene();
    }
}
