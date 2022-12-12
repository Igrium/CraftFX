package com.igrium.craftfx.viewport;

import com.igrium.craftfx.CraftFX;
import com.igrium.craftfx.engine.MovementHandler;

import javafx.scene.Scene;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

/**
 * Handles keystrokes on an engine viewport and translating them to the engine.
 */
public abstract class InputController<T extends EngineViewport, M extends MovementHandler> implements AutoCloseable, WorldRenderEvents.Start {
    protected final T viewport;
    protected final M movementHandler;

    public InputController(T viewport, M movementHandler) {
        this.viewport = viewport;
        this.movementHandler = movementHandler;
        CraftFX.START_RENDER_WEAK.register(this);
        initListeners(viewport);
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

    private long lastTick;
    
    @Override
    public final void onStart(WorldRenderContext context) {
        long now = System.currentTimeMillis();
        long delta = now - lastTick;
        if (delta == 0) return;

        tick(delta);

        lastTick = now;
    }

    @Override
    public void close() {
        CraftFX.START_RENDER_WEAK.unregister(this);
    }
}
