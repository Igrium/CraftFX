package com.igrium.craftfx.engine;

import com.igrium.craftfx.viewport.EngineViewportHandle;

/**
 * <p>
 * Provides a viewport into the Minecraft engine which can be rendered to a
 * viewport handle.
 * </p>
 * <p>
 * Each viewport provider can have multiple handles attached, but it can only
 * obtain the desired resolution from one. If there is more than handle, the
 * handle chosen for the resolution is undefined (although persistent across
 * frames)
 * </p>
 */
public interface ViewportProvider {

    /**
     * Add a handle to this viewport provider.
     * @param handle The handle.
     */
    public void addHandle(EngineViewportHandle handle);

    /**
     * Remove a handle from this viewport provider.
     * @param handle The handle.
     * @return If the handle was found.
     */
    public boolean removeHandle(EngineViewportHandle handle);

    /**
     * Whether this provider is currently active.
     */
    public boolean isActive();
}
