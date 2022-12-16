package com.igrium.craftfx.engine;

import java.util.Optional;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * A movement handler for players that can apply arbitrary movement to the player.
 * Not guarenteed to be compatible with servers.
 */
public class ArbitraryPlayerMovementHandler extends PlayerMovementHandler {

    public ArbitraryPlayerMovementHandler(ClientPlayerEntity player, GameOptions settings) {
        super(player, settings);
    }

    @Override
    public boolean supportsArbitraryMovement() {
        return true;
    }

    @Override
    public void setPos(Vec3d pos) throws UnsupportedOperationException {
        Vec3d vec = correctEyeheight(pos, player);

        // Prevent unwanted interpolation
        player.prevX = vec.x;
        player.prevY = vec.y;
        player.prevZ = vec.z;

        player.setPos(vec.x, vec.y, vec.z);
    }

    @Override
    public Vec3d getPos() { // Re-implement so we know it won't be incompatible with setPos
        return new Vec3d(player.getX(), player.getY() + player.getEyeHeight(player.getPose()), player.getZ());
    }

    private static Vec3d correctEyeheight(Vec3d vec, Entity entity) {
        return new Vec3d(vec.x, vec.y - entity.getEyeHeight(entity.getPose()), vec.z);
    }

    /**
     * Create and setup a movement handler on the local player.
     * @return The movement handler.
     */
    public static MutablePlayerMovementHandler<ArbitraryPlayerMovementHandler> createDefaultArbitrary() {
        MinecraftClient client = MinecraftClient.getInstance();
        return new MutablePlayerMovementHandler<>(() -> client.player, player -> {
            ArbitraryPlayerMovementHandler handler = new ArbitraryPlayerMovementHandler(player, client.options);
            client.execute(() -> player.input = handler);
            return handler;
        }, Optional.of(true));
    }
}
