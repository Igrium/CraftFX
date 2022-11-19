package com.igrium.craftfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igrium.craftfx.application.ApplicationManager;

import net.fabricmc.api.ClientModInitializer;

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
    }
    
    /**
     * Get the active <code>ApplicationManager</code> instance.
     * @return The application manager.
     */
    public ApplicationManager getApplicationManager() {
        return applicationManager;
    }
}
