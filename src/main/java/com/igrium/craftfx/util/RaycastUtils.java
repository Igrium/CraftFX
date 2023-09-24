package com.igrium.craftfx.util;

import java.util.function.Predicate;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

/**
 * A set of utility functions involving raycasting.
 */
public final class RaycastUtils {
    private RaycastUtils() {};

    /**
     * Perform a raycast based on a specific point in screenspace.
     * 
     * @param x             Screenspace X.
     * @param y             Screenspace Y.
     * @param width         Width of the viewport.
     * @param height        Height of the viewport.
     * @param distance      Max distance of the raycast.
     * @param predicate     A filter of which entities are allowed for this raycast.
     * @param includeFluids Whether to include fluids or not.
     * @return The hit result.
     */
    public static HitResult raycastViewport(float x, float y, float width, float height, float distance,
            Predicate<Entity> predicate, boolean includeFluids) {
        Entity cameraEntity = RenderUtils.getCameraEntity();
        Camera camera = RenderUtils.getCamera();

        Vec3d start = camera.getPos();
        Vec3d end = projectViewport(camera, x, y, width, height, distance);

        return raycast(cameraEntity, start, end, predicate, includeFluids);
    }

    /**
     * Using the given camera, determine the world location of a specific point in
     * screenspace.
     * 
     * @param camera   The camera to use.
     * @param x        Screenspace X
     * @param y        Screenspace Y
     * @param width    Width of the viewport.
     * @param height   Height of the viewport.
     * @param distance Max distance of the projection.
     * @return A point in 3D space that falls under the 2D screenspace point.
     */
    public static Vec3d projectViewport(Camera camera, float x, float y, float width, float height, float distance) {
        Vector4f screenspace = new Vector4f(2 * x / width - 1, 2 * y / height - 1, -1, 1);

        Matrix4f cameraProjection = new Matrix4f(RenderUtils.getCameraProjection());
        cameraProjection.invert();

        // screenspace.transform(cameraProjection);
        cameraProjection.transform(screenspace);
        screenspace.mul(-distance); // Bandaid on ray that's broken for some reason.
        screenspace.rotate(camera.getRotation());
        screenspace.add((float) camera.getPos().x, (float) camera.getPos().y, (float) camera.getPos().z, 0);

        return new Vec3d(screenspace.x(), screenspace.y(), screenspace.z());
    }

    /**
     * Perform a simple raycast on the block world and its entities.
     * 
     * @param sourceEntity  Source entity to pass to Minecraft's raycasting
     *                      functions.
     * @param start         Raycast start point.
     * @param end           Raycast end point.
     * @param predicate     A filter of which entities are allowed for this raycast.
     * @param includeFluids Whether to include fluids or not.
     * @return The hit result.
     */
    public static HitResult raycast(Entity sourceEntity, Vec3d start, Vec3d end, Predicate<Entity> predicate, boolean includeFluids) {
        double distance = start.distanceTo(end);
        Box box = Box.of(start, 1, 1, 1)
                .stretch(sourceEntity.getRotationVec(1).multiply(distance))
                .expand(1, 1, 1);

        BlockHitResult worldHit = sourceEntity.getWorld().raycast(new RaycastContext(
                start,
                end,
                ShapeType.OUTLINE,
                includeFluids ? FluidHandling.ANY : FluidHandling.NONE,
                sourceEntity));

        EntityHitResult entityHit = ProjectileUtil.raycast(sourceEntity, start, end, box, predicate, distance);

        if (entityHit != null) {
            double entityDist = start.squaredDistanceTo(entityHit.getPos());
            double worldDist = start.squaredDistanceTo(worldHit.getPos());

            if (entityDist < worldDist) {
                return entityHit;
            }
        }

        return worldHit;
    }
}
