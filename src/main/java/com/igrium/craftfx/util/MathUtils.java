package com.igrium.craftfx.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

/**
 * Contains a number of static utility functions pertaining to math.
 */
public final class MathUtils {
    private MathUtils() {}
    
    /**
     * Multiply two quaternion rotations. The resulting equation is <code>dest = left * right</code>
     * @param left The left quaternion.
     * @param right The right quaternion.
     * @param dest A quaternion object to store the result.
     * @return <code>dest</code>
     */
    // Note: I basically copied this from Joml. I have no idea what this math means.
    public static Quaternion mulQuaternions(Quaternion left, Quaternion right, Quaternion dest) {
        float lw = left.getW();
        float lx = left.getX();
        float ly = left.getY();
        float lz = left.getZ();

        float rw = right.getW();
        float rx = right.getX();
        float ry = right.getY();
        float rz = right.getZ();

        float x = Math.fma(lw, rx, Math.fma(lx, rw, Math.fma(ly, rz, -lz * ry)));
        float y = Math.fma(lw, ry, Math.fma(-lx, rz, Math.fma(ly, rw, lz * rx)));
        float z = Math.fma(lw, rz, Math.fma(lx, ry, Math.fma(-ly, rx, lz * rw)));
        float w = Math.fma(lw, rw, Math.fma(-lx, rx, Math.fma(-ly, ry, -lz * rz)));

        dest.set(x, y, z, w);
        return dest;
    }

    public static final Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180);
        float g = -yaw * ((float)Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }
}
