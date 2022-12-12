package com.igrium.craftfx.viewport;

import java.util.function.Supplier;

import net.minecraft.util.math.Vec3d;

/**
 * A simple, reusable class that manages the inner-workings of first-person flight controls.
 */
public class FirstPersonController {
    private double speed = 10;
    protected Supplier<Float> pitchSupplier;
    protected Supplier<Float> yawSupplier;

    private float forwardAmount;
    private float leftAmount;
    private float upAmount;

    public boolean pressingForward;
    public boolean pressingBackward;
    public boolean pressingLeft;
    public boolean pressingRight;
    public boolean pressingUp;
    public boolean pressingDown;

    private boolean autoClamp = true;

    /**
     * Create a first person controller.
     * @param pitchSupplier A function to retrieve the current pitch of the camera.
     * @param yawSupplier A function to retrieve the current yaw of the camera.
     */
    public FirstPersonController(Supplier<Float> pitchSupplier, Supplier<Float> yawSupplier) {
        this.pitchSupplier = pitchSupplier;
        this.yawSupplier = yawSupplier;
    }

    /**
     * Whether this controller should auto-clamp movement values.
     */
    public boolean shouldAutoClamp() {
        return autoClamp;
    }

    /**
     * Set whether this controller should auto-clamp movement values.
     */
    public void setAutoClamp(boolean autoClamp) {
        this.autoClamp = autoClamp;
    }

    public void setForwardAmount(float forwardAmount) {
        this.forwardAmount = autoClamp ? clamp(-1f, 1f, forwardAmount) : forwardAmount;
    }

    public final float getForwardAmount() {
        return forwardAmount;
    }

    public void setLeftAmount(float leftAmount) {
        this.leftAmount = autoClamp ? clamp(-1f, 1f, leftAmount) : leftAmount;
    }

    public final float getLeftAmount() {
        return leftAmount;
    }

    public void setUpAmount(float upAmount) {
        this.upAmount = autoClamp ? clamp(-1f, 1f, upAmount) : upAmount;;
    }

    public final float getUpAmount() {
        return upAmount;
    }

    /**
     * Get the speed of this controller.
     * @return The speed, in blocks per second.
     */
    public final double getSpeed() {
        return speed;
    }

    /**
     * Set the speed of this controller.
     * @param maxSpeed The new speed, in blocks per second.
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Update the movement scalars based on the keyboard state.
     */
    public void updateKeyboard() {
        setForwardAmount(getMovementMultiplier(pressingForward, pressingBackward));
        setLeftAmount(getMovementMultiplier(pressingLeft, pressingRight));
        setUpAmount(getMovementMultiplier(pressingUp, pressingDown));
    }
    
    /**
     * Calculate the movement vector for this tick.
     * @param delta The number of milliseconds since the last tick.
     * @return The vector to add to the camera's position this tick.
     */
    public Vec3d getMovementVector(long delta) {
        Vec3d vec = new Vec3d(getLeftAmount(), getUpAmount(), getForwardAmount());
        vec = vec.rotateX(pitchSupplier.get());
        vec = vec.rotateY(yawSupplier.get());

        double deltaSeconds = delta / 1000d;
        double distance = deltaSeconds * speed;

        vec = vec.multiply(distance);

        return vec;
    }

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0f;
        }
        return positive ? 1.0f : -1.0f;
    }

    private static float clamp(float min, float max, float value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }
}
