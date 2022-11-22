package com.igrium.craftfx.viewport;

import java.nio.ByteBuffer;

/**
 * A handle allowing JavaFX to recieve render events from the Minecraft engine.
 */
public interface EngineViewportHandle {

    /**
     * Called whenever the image buffer has been (re-)allocated.
     * @param buffer The new buffer.
     * @param width The width of the frame.
     * @param height The height of the frame.
     */
    public void onAllocate(ByteBuffer buffer, int width, int height);

    /**
     * Called when the viewport has been updated.
     * @param buffer A reference to the image buffer.
     */
    public void update(ByteBuffer buffer);

    public int getDesiredWidth();
    public int getDesiredHeight();
}
