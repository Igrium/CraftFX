package com.igrium.craftfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igrium.craftfx.application.ApplicationManager;
import com.igrium.craftfx.engine.PrimaryViewportProvider;
import com.igrium.craftfx.util.RenderUtils;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

/**
 * The primary mod initializer for CraftFX
 */
public class CraftFX implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
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

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
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
