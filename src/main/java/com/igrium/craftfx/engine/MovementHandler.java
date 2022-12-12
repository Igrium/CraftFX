package com.igrium.craftfx.engine;

import org.jetbrains.annotations.Nullable;

import com.igrium.craftfx.viewport.InputController;

import net.minecraft.util.math.Vec3d;

/**
 * Responsible for reading movement controls and executing them on the camera.
 * All of these methods may be called on any thread, and they should be executed
 * on the next frame.
 */
public interface MovementHandler {

    /**
     * Determine whether arbitrary movement is supported via {@code setPos}
     * @return Is arbitrary movement supported?
     */
    default boolean supportsArbitraryMovement() {
        return false;
    }

    /**
     * Set the camera's position.
     * 
     * @param pos The new camera position.
     * @throws UnsupportedOperationException If this movement handler doesn't
     *                                       support arbitrary movement.
     */
    default void setPos(Vec3d pos) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    void setForwardAmount(float amount);
    void setSidewaysAmount(float amount);
    void setJumping(boolean jumping);
    void setSneaking(boolean sneaking);

    void setPitch(float pitch);
    void setYaw(float yaw);
    void changeLookDirection(double dx, double dy);

    default void setRotation(float yaw, float pitch) {
        setYaw(yaw);
        setPitch(pitch);
    }

    Vec3d getPos();
    float getPitch();
    float getYaw();

    /**
     * Attach a movement controller to this handler. The controllers
     * <code>tick()</code> function should be called every frame the handler is
     * active after this is called.
     * 
     * @param controller The controller to use. <code>null</code> to clear the
     *                   controller.
     */
    void setController(@Nullable InputController<?, ?> controller);
}
