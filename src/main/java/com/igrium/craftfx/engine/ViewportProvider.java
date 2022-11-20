package com.igrium.craftfx.engine;

import org.jetbrains.annotations.Nullable;

import com.igrium.craftfx.viewport.EngineViewportHandle;

/**
 * Provides a viewport into the Minecraft engine which can be rendered to a viewport handle.
 */
public interface ViewportProvider {
    /**
     * Set the viewport handle being rendered to.
     * @param handle New viewport handle.
     */
    public void setHandle(@Nullable EngineViewportHandle handle);

    /**
     * Get the current viewport handle.
     * @return Current viewport handle.
     */
    @Nullable
    public EngineViewportHandle getHandle();

    /**
     * Whether this provider is currently active.
     */
    public boolean isActive();
}
