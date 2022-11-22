package com.igrium.craftfx.viewport;

import com.igrium.craftfx.engine.PrimaryViewportProvider;

/**
 * A viewport that displays the contents primary Minecraft window.
 */
public class PrimaryViewport extends EngineViewport {

    public PrimaryViewport() {
        visibleProperty().addListener((prop, oldVal, newVal) -> update());
        parentProperty().addListener((prop, oldVal, newVal) -> update());
    }

    protected void update() {
        boolean visible = visibleProperty().get() && parentProperty().get() != null;
        PrimaryViewportProvider provider = PrimaryViewportProvider.getInstance();

        if (visible) {
            setViewportProvider(provider);
            provider.setCustomResolution(true);
        } else {
            setViewportProvider(null);
        }
    }
}
