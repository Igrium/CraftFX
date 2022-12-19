package com.igrium.craftfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igrium.craftfx.application.ApplicationManager;
import com.igrium.craftfx.engine.PrimaryViewportProvider;
import com.igrium.craftfx.events.GameRenderEvents;
import com.igrium.craftfx.util.RenderUtils;
import com.igrium.craftfx.util.WeakRegisterEvent;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;

/**
 * The primary mod initializer for CraftFX
 */
public class CraftFX implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final WeakRegisterEvent<StartTick> START_CLIENT_TICK_WEAK = new WeakRegisterEvent<>(
            ClientTickEvents.START_CLIENT_TICK, MinecraftClient.class, con -> con::accept, (listener, params) -> listener.onStartTick(params));
    
    public static final WeakRegisterEvent<WorldRenderEvents.Start> START_RENDER_WEAK = new WeakRegisterEvent<>(
        WorldRenderEvents.START, WorldRenderContext.class, con -> con::accept, (listener, context) -> listener.onStart(context));

    private static CraftFX instance;

    private ApplicationManager applicationManager;

    /**
     * Get the active mod instance.
     * @return Mod instance.
     */
    public static CraftFX getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        applicationManager = new ApplicationManager(this);
        
        GameRenderEvents.PRE_RENDER.register(tick -> {
            PrimaryViewportProvider.getInstance().pretick();
        });

        WorldRenderEvents.AFTER_SETUP.register(RenderUtils::updateRenderContext);

        WorldRenderEvents.END.register(context -> {
            PrimaryViewportProvider.getInstance().update();
        });
    }
    
    /**
     * Get the active <code>ApplicationManager</code> instance.
     * @return The application manager.
     */
    public ApplicationManager getApplicationManager() {
        return applicationManager;
    }
}
