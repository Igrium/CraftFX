package com.igrium.craftfx.engine;

import org.jetbrains.annotations.Nullable;

import com.igrium.craftfx.viewport.InputController;

import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.Vec3d;

/**
 * A re-implementation of <code>KeyboardInput</code> that allows for manual overriding in code.
 */
public class PlayerMovementHandler extends KeyboardInput implements MovementHandler {
    protected final ClientPlayerEntity player;

    private boolean ignoreNative = false;

    private float forwardAmount;
    private float sidewaysAmount;
    private boolean isJumping;
    private boolean isSneaking;

    @Nullable
    private InputController<?, ?> controller;
    /**
     * Create a player movement handler.
     * @param player The player to use.
     * @param input The player's input manager. Must be set before handler can be used.
     */
    public PlayerMovementHandler(ClientPlayerEntity player, GameOptions settings) {
        super(settings);
        this.player = player;
    }

    public ClientPlayerEntity getPlayer() {
        return player;
    }

    @Override
    public void setForwardAmount(float amount) {
        forwardAmount = amount;
    }

    @Override
    public void setSidewaysAmount(float amount) {
        sidewaysAmount = amount;
    }

    @Override
    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    @Override
    public void setSneaking(boolean sneaking) {
        isSneaking = sneaking;
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

    public synchronized void setIgnoreNative(boolean ignoreNative) {
        this.ignoreNative = ignoreNative;
        if (ignoreNative) {
            pressingForward = false;
            pressingBack = false;
            pressingLeft = false;
            pressingRight = false;
        }
    }

    /**
     * Should Minecraft's native input system be ignored?
     */
    public boolean shouldIgnoreNative() {
        return ignoreNative;
    }

    @Override
    public void setController(@Nullable InputController<?, ?> controller) {
        this.controller = controller;
    }

    private long lastTick = System.currentTimeMillis();

    @Override
    public void tick(boolean slowDown, float f) {
        if (!ignoreNative) {
            super.tick(slowDown, f);
        }

        movementForward += forwardAmount;
        movementForward = clamp(movementForward);

        movementSideways += sidewaysAmount;
        movementSideways = clamp(movementSideways);

        this.jumping = isJumping || (!ignoreNative && jumping);
        this.sneaking = isSneaking || (!ignoreNative && sneaking);

        // Tick controller
        long now = System.currentTimeMillis();
        
        if (controller != null && now - lastTick != 0) { // To avoid devide by zero errors
            controller.tick(now - lastTick);
        }

        lastTick = now;
    }

    private float clamp(float val) {
        if (val > 1f) return 1f;
        if (val < -1f) return -1f;
        return val;
    }
}
