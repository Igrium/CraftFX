package com.igrium.craftfx.viewport.movement;

import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

/**
 * A movement controller that uses mouse motion to perform orbit/pan/zoom movements.
 */
public abstract class MouseMovementController {
    protected final Supplier<Vec3d> posSupplier;
    protected final Supplier<Float> pitchSupplier;
    protected final Supplier<Float> yawSupplier;

    private Vec3d focusPoint = new Vec3d(0, 0, 0);

    public MouseMovementController(Supplier<Vec3d> posSupplier, Supplier<Float> pitchSupplier, Supplier<Float> yawSupplier) {
        this.posSupplier = posSupplier;
        this.pitchSupplier = pitchSupplier;
        this.yawSupplier = yawSupplier;
    }

    public final Vec3d getFocusPoint() {
        return focusPoint;
    }

    public void setFocusPoint(Vec3d focusPoint) {
        this.focusPoint = focusPoint;
    }

    // protected Vec3d getCameraFacingVector() {
    //     return new Vec3d(0, 0, 1).rotateX(pitchSupplier.get()).rotateY(yawSupplier.get());
    // }

    protected Vec3f getCameraFacingVector() {
        Vec3f vec = new Vec3f(0, 0, 1);
        vec.rotate(getCameraRotation());
        return vec;
    }

    protected Quaternion getCameraRotation() {
        return Quaternion.fromEulerYxz(pitchSupplier.get() * MathHelper.RADIANS_PER_DEGREE,
                yawSupplier.get() * MathHelper.RADIANS_PER_DEGREE, 0);
    }

    /**
     * Update the camera position for a given mouse movement.
     * @param dx Mouse delta X.
     * @param dy Mouse delta Y.
     * @return [new position, new pitch, new yaw]
     */
    public abstract Triple<Vec3d, Float, Float> update(double dx, double dy);
}
