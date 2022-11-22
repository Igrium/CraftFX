package com.igrium.craftfx.engine;

import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;

/**
 * A re-implementation of <code>KeyboardInput</code> that allows for manual overriding in code.
 */
public class ManualKeyboardInput extends KeyboardInput {

    private boolean ignoreNative = false;

    private float forwardAmount;
    private float sidewaysAmount;
    private boolean isJumping;
    private boolean isSneaking;

    public synchronized void setForwardAmount(float forwardAmount) {
        this.forwardAmount = forwardAmount;
    }

    public synchronized void setSidewaysAmount(float sidewaysAmount) {
        this.sidewaysAmount = sidewaysAmount;
    }

    public synchronized void setJumping(boolean jumping) {
        this.isJumping = jumping;
    }

    public synchronized void setSneaking(boolean sneaking) {
        this.isSneaking = sneaking;
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

    public ManualKeyboardInput(GameOptions settings) {
        super(settings);
    }

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
    }

    private float clamp(float val) {
        if (val > 1f) return 1f;
        if (val < -1f) return -1f;
        return val;
    }
}
