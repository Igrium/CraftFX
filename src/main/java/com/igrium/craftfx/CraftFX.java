package com.igrium.craftfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igrium.craftfx.application.ApplicationManager;
import com.igrium.craftfx.engine.PrimaryViewportProvider;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class CraftFX implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    private static CraftFX instance;

    private ApplicationManager applicationManager;

    public static CraftFX getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        applicationManager = new ApplicationManager(this);

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
