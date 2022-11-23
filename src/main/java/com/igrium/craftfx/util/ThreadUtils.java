package com.igrium.craftfx.util;

import com.mojang.blaze3d.systems.RenderSystem;

import javafx.application.Platform;

public final class ThreadUtils {
    /**
     * Execute a render call ASAP on the render thread.
     * @param r The render call.
     */
    public static void onRenderThread(Runnable r) {
        if (RenderSystem.isOnRenderThread()) {
            r.run();
            return;
        } else {
            RenderSystem.recordRenderCall(r::run);
        }
    }

    /**
     * Execute ASAP on the JavaFX thread.
     * @param r The runnable.
     */
    public static void onFXThread(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
            return;
        } else {
            Platform.runLater(r);
        }
    }
}
