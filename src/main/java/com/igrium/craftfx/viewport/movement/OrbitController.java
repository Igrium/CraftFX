package com.igrium.craftfx.viewport.movement;

import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Triple;

import com.igrium.craftfx.util.MathUtils;

import net.minecraft.util.math.Vec3d;

/**
 * A simple, re-usable class that manages orbit/pan/zoom controls of a viewport.
 * @see FirstPersonController
 */
public class OrbitController extends MouseMovementController {

    private final float MULTIPLER = .25f;

    public OrbitController(Supplier<Vec3d> posSupplier, Supplier<Float> pitchSupplier, Supplier<Float> yawSupplier) {
        super(posSupplier, pitchSupplier, yawSupplier);
    }

    // @Override
    // public Triple<Vec3d, Float, Float> update(double dx, double dy) {
    //     // Vec3d pos = posSupplier.get();
    //     // Vec3d relative = pos.subtract(getFocusPoint());
        
    //     float pitch = (float) (dy * MULTIPLER);
    //     float yaw = (float) (dx * MULTIPLER);

    //     // relative = relative.rotateX(-pitch * MathHelper.RADIANS_PER_DEGREE);
    //     // relative = relative.rotateY(-yaw * MathHelper.RADIANS_PER_DEGREE);

    //     Quaternion rotation = Quaternion.fromEulerYxz(pitch * MathHelper.RADIANS_PER_DEGREE, yaw * MathHelper.RADIANS_PER_DEGREE, 0);
    //     Vec3f focusPoint = new Vec3f(getFocusPoint());

    //     Vec3f pos = new Vec3f(posSupplier.get());

    //     pos.subtract(focusPoint);
    //     pos.rotate(rotation);
    //     pos.add(focusPoint);

    //     Quaternion localRot = getCameraRotation();
    //     MathUtils.mulQuaternions(rotation, localRot, localRot);
        
    //     Vec3f finalRot = localRot.toEulerYxzDegrees();

    //     return Triple.of(new Vec3d(pos), pitchSupplier.get() + pitch, yawSupplier.get() + yaw);


    //     // return Triple.of(relative.add(getFocusPoint()), pitchSupplier.get() + pitch, yawSupplier.get() + yaw);
    // }

    @Override
    // https://www.mbsoftworks.sk/tutorials/opengl4/026-camera-pt3-orbit-camera/
    public Triple<Vec3d, Float, Float> update(double dx, double dy) {

        // Identify center point
        Vec3d currentPos = posSupplier.get();
        double radiusSquared = currentPos.squaredDistanceTo(getFocusPoint());
        double radius = Math.sqrt(radiusSquared);

        Vec3d currentDirection = MathUtils.getRotationVector(pitchSupplier.get(), yawSupplier.get());
        Vec3d center = currentDirection.multiply(radius);
        setFocusPoint(center);

        // Convert to polar coordinates

        // Vec3d localPos = currentPos.subtract(center);
        double polar = Math.toRadians(pitchSupplier.get());
        double azimuth = Math.toRadians(yawSupplier.get());
        // double angle = Math.atan2(localPos.z, -localPos.x);
        // double azimuth = Math.acos(localPos.y / radius);

        polar += Math.toRadians(dx * MULTIPLER);
        azimuth += Math.toRadians(dy * MULTIPLER);

        double polarCap = Math.PI / 2 - .001;
        if (polar > polarCap) {
            polar = polarCap;
        } else if (azimuth < -polarCap) {
            polar = polarCap;
        }

        double sinAzimuth = Math.sin(azimuth);
        double cosAzimuth = Math.cos(azimuth);
        double sinPolar = Math.sin(polar);
        double cosPolar = Math.cos(polar);

        Vec3d newPos = new Vec3d(
                center.x + radius * cosPolar * cosAzimuth,
                center.y + radius * sinPolar,
                center.z + radius * cosPolar * sinAzimuth);

        // return Triple.of(newPos, (float) Math.toDegrees(azimuth), (float) Math.toDegrees(polar));
        return Triple.of(newPos, 0f, 0f);
    }
    
}
