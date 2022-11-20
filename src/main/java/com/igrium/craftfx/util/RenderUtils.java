package com.igrium.craftfx.util;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Matrix4f;

/**
 * A set of utility functions involving the rendering of the Minecraft game.
 */
public final class RenderUtils {
    private RenderUtils() {};

    private static Matrix4f cameraProjection;
    private static Camera camera;

    /**
     * Internal use only
     */
    public static void updateRenderContext(WorldRenderContext context) {
        cameraProjection = context.projectionMatrix().copy();
        camera = context.camera();
    }

    /**
     * Get the primary camera projection matrix on the last frame that was rendered.
     * @return The projection matrix.
     */
    public static Matrix4f getCameraProjection() {
        return cameraProjection;
    }

    /**
     * Get the camera that was used to render the last frame.
     * @return The active camera.
     */
    public static Camera getCamera() {
        return camera;
    }

    /**
     * Get the camera entity that was used to render the last frame.
     * @return The current camera entity.
     */
    public static Entity getCameraEntity() {
        // `camera.getFocusedEntity` returns the camera entity upon code inspection.
        // I have no idea why Yarn decided to call it the "focused entity".
        return camera == null ? null : camera.getFocusedEntity();
    }
}
