package com.igrium.craftfx.viewport;

import java.io.Closeable;

/**
 * Handles keystrokes on an engine viewport and translating them to the engine.
 */
public abstract class InputController<T extends EngineViewport> implements Closeable {
    private final T viewport;

    public InputController(T viewport) {
        this.viewport = viewport;
        initListeners(viewport);
    }

    protected abstract void initListeners(T viewport);

    public T getViewport() {
        return viewport;
    }
}
