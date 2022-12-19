package com.igrium.craftfx.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A set of events pertaining to rendering outside the world renderer.
 */
@Environment(EnvType.CLIENT)
public final class GameRenderEvents {
    private GameRenderEvents() {};

    /**
     * Called before each frame is rendered.
     */
    public static final Event<PreRender> PRE_RENDER = EventFactory.createArrayBacked(PreRender.class,
            listeners -> tick -> {
                for (PreRender listener : listeners) {
                    listener.onPreRender(tick);
                }
            });
    

    /**
     * Called at the very beginning of each frame.
     */
    public static final Event<Start> START = EventFactory.createArrayBacked(Start.class,
            listeners -> context -> {
                for (Start listener : listeners) {
                    listener.onStart(context);
                }
            });
    
    
    @FunctionalInterface
    public interface PreRender {
        void onPreRender(boolean tick);
    }
    
    @FunctionalInterface
    public interface Start {
        void onStart(GameRenderContext context);
    }
}
