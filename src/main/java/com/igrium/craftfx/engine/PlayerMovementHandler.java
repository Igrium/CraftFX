package com.igrium.craftfx.engine;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * A movement handler that applies movement directly to a player entity.
 */
public class PlayerMovementHandler implements MovementHandler {
    private final ClientPlayerEntity player;
    private final ManualKeyboardInput input;

    /**
     * Create a player movement handler.
     * @param player The player to use.
     * @param input The player's input manager. Must be set before handler can be used.
     */
    public PlayerMovementHandler(ClientPlayerEntity player, ManualKeyboardInput input) {
        this.input = input;
        this.player = player;
    }

    public ClientPlayerEntity getPlayer() {
        return player;
    }

    public ManualKeyboardInput getInput() {
        return input;
    }

    @Override
    public void setForwardAmount(float amount) {
        input.setForwardAmount(amount);
    }

    @Override
    public void setSidewaysAmount(float amount) {
        input.setSidewaysAmount(amount);
    }

    @Override
    public void setJumping(boolean jumping) {
        input.setJumping(jumping);
    }

    @Override
    public void setSneaking(boolean sneaking) {
        input.setSneaking(sneaking);
    }

    @Override
    public void setPitch(float pitch) {
        player.setPitch(pitch);
    }

    @Override
    public void setYaw(float yaw) {
        player.setYaw(yaw);
    }

    @Override
    public void changeLookDirection(double dx, double dy) {
        player.changeLookDirection(dx, dy);
    }

    @Override
    public Vec3d getPos() {
        return player.getCameraPosVec(1);
    }

    @Override
    public float getPitch() {
        return player.getPitch();
    }

    @Override
    public float getYaw() {
        return player.getYaw();
    }
}
