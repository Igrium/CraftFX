package com.igrium.craftfx.engine;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * A player movement handler that uses the result of a supplier rather than a
 * set player. Useful for controlling the local player, which may change over
 * the course of a session.
 */
public class MutablePlayerMovementHandler<T extends MovementHandler> implements MovementHandler {

    protected final Supplier<ClientPlayerEntity> playerSupplier;
    protected final Function<ClientPlayerEntity, T> factory;

    /**
     * The player that was used on the last update.
     */
    @Nullable
    protected ClientPlayerEntity player;

    /**
     * The base movement handler from the last update.
     */
    @Nullable
    protected T movementHandler;

    /**
     * An optional override of whether this movement handler supports arbitrary movement.
     */
    protected Optional<Boolean> supportsArbitraryMovement = Optional.empty();
    
    /**
     * Create a player movement handler that uses the result of a supplier rather
     * than a set player.
     * 
     * @param player  The player supplier. May return
     *                <code>null</code>.
     * @param factory A factory that creates the underlying
     *                movement handler whenever the player is
     *                changed.
     */
    public MutablePlayerMovementHandler(Supplier<ClientPlayerEntity> player, Function<ClientPlayerEntity, T> factory) {
        this(player, factory, Optional.empty());
    }

    /**
     * Create a player movement handler that uses the result of a supplier rather
     * than a set player.
     * 
     * @param player                    The player supplier. May return
     *                                  <code>null</code>.
     * @param factory                   A factory that creates the underlying
     *                                  movement handler whenever the player is
     *                                  changed.
     * @param supportsArbitraryMovement An optional override on whether this handler
     *                                  says it supports arbitrary movement.
     */
    public MutablePlayerMovementHandler(Supplier<ClientPlayerEntity> player, Function<ClientPlayerEntity, T> factory, Optional<Boolean> supportsArbitraryMovement) {
        this.playerSupplier = player;
        this.factory = factory;
        this.supportsArbitraryMovement = supportsArbitraryMovement;
        updatePlayer();
    }

    protected void updatePlayer() {
        ClientPlayerEntity player = playerSupplier.get();
        if (this.player == player) {
            return;
        } if (player == null) {
            movementHandler = null;
            return;
        }
        movementHandler = factory.apply(player);
        this.player = player;
    }

    /**
     * Get the current player that's in use.
     * @return The current player.
     */
    @Nullable
    public ClientPlayerEntity getPlayer() {
        updatePlayer();
        return player;
    }

    /**
     * Get the movement handler that was used the last time this handler was updated.
     * @return The most recent movement handler.
     */
    @Nullable
    public T getMovementHandler() {
        return movementHandler;
    }

    @Override
    public void setForwardAmount(float amount) {
        updatePlayer();
        if (movementHandler != null) movementHandler.setForwardAmount(amount);
    }

    @Override
    public void setSidewaysAmount(float amount) {
        updatePlayer();
        if (movementHandler != null) movementHandler.setSidewaysAmount(amount);
    }

    @Override
    public void setJumping(boolean jumping) {
        updatePlayer();
        if (movementHandler != null) movementHandler.setJumping(jumping);
    }

    @Override
    public void setSneaking(boolean sneaking) {
        updatePlayer();
        if (movementHandler != null) movementHandler.setSneaking(sneaking);
    }

    @Override
    public void setPitch(float pitch) {
        updatePlayer();
        if (movementHandler != null) movementHandler.setPitch(pitch);
    }

    @Override
    public void setYaw(float yaw) {
        updatePlayer();
        if (movementHandler != null) movementHandler.setYaw(yaw);
    }

    @Override
    public void changeLookDirection(double dx, double dy) {
        updatePlayer();
        if (movementHandler != null) movementHandler.changeLookDirection(dx, dy);
    }

    @Override
    public Vec3d getPos() {
        return movementHandler != null ? movementHandler.getPos() : Vec3d.ZERO;
    }

    @Override
    public float getPitch() {
        return movementHandler != null ? movementHandler.getPitch() : 0;
    }

    @Override
    public float getYaw() {
        return movementHandler != null ? movementHandler.getYaw() : 0;
    }

    @Override
    public boolean supportsArbitraryMovement() {
        if (supportsArbitraryMovement.isPresent()) return supportsArbitraryMovement.get();
        // Null movement handlers support arbitrary movement to prevent illegal state during init
        return movementHandler == null || movementHandler.supportsArbitraryMovement();
    }

    @Override
    public void setPos(Vec3d pos) throws UnsupportedOperationException {
        if (movementHandler != null) movementHandler.setPos(pos);
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        if (movementHandler != null) movementHandler.setRotation(yaw, pitch);
    }
    
}
