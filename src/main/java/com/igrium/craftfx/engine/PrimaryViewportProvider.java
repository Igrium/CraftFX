package com.igrium.craftfx.engine;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;

import com.igrium.craftfx.viewport.EngineViewportHandle;
import com.mojang.blaze3d.systems.RenderSystem;

import javafx.application.Platform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Window;

/**
 * Exports the main Minecraft window into an engine viewport.
 */
public class PrimaryViewportProvider implements ViewportProvider {
    private PrimaryViewportProvider() {};
    private static PrimaryViewportProvider instance;

    public static PrimaryViewportProvider getInstance() {
        if (instance == null) {
            instance = new PrimaryViewportProvider();
        }
        return instance;
    }

    private int currentX;
    private int currentY;
    private ByteBuffer buffer;

    private boolean needsCleanup;

    private boolean customResolution = true;

    // protected EngineViewportHandle handle;
    protected final List<EngineViewportHandle> handles = new ArrayList<>();
    MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public synchronized void addHandle(EngineViewportHandle handle) {
        handles.add(handle);
    }

    @Override
    public synchronized boolean removeHandle(EngineViewportHandle handle) {
        return handles.remove(handle);
    }

    public synchronized void update() {
        RenderSystem.assertOnRenderThread();
        if (handles.isEmpty()) {
            if (buffer != null) {
                clearBuffer(buffer);
                buffer = null;
            }
            return;
        };

        Framebuffer frameBuffer = MinecraftClient.getInstance().getFramebuffer();
        int x = frameBuffer.textureWidth; int y = frameBuffer.textureHeight;
        
        // Reallocate
        if (buffer == null || x != currentX || y != currentY) {
            if (buffer != null) {
                clearBuffer(buffer);
            }

            currentX = x;
            currentY = y;
            buffer = MemoryUtil.memAlloc(x * y  * 4);

            handles.forEach(handle -> handle.onAllocate(buffer, x, y));
        }

        RenderSystem.bindTexture(frameBuffer.getColorAttachment());
        RenderSystem.readPixels(0, 0, x, y, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);
        buffer.rewind();

        handles.forEach(handle -> handle.update(buffer));
    }

    // Buffers are cleared on the JavaFX thread so we don't pull them out from under the JavaFX renderer.
    private void clearBuffer(ByteBuffer ptr) {
        Platform.runLater(() -> MemoryUtil.memFree(ptr));
    }

    public synchronized void pretick() {
        if (handles.isEmpty()) {
            if (needsCleanup) {
                resetFramebuffer();
                needsCleanup = false;
            }
            return;
        };

        int x = handles.get(0).getDesiredWidth();
        int y = handles.get(0).getDesiredHeight();

        if (x <= 0 || y <= 0) return;

        Framebuffer fb = client.getFramebuffer();
        if (customResolution && (fb.viewportWidth != x || fb.viewportHeight != y)) {
            fb.resize(x, y, false);

            Window window = client.getWindow();
            window.setFramebufferWidth(x);
            window.setFramebufferHeight(y);

            client.gameRenderer.onResized(x, y);
        }
        needsCleanup = true;
    }

    /**
     * Whether the primary viewport provider is allowed update the resolution of the
     * framebuffer.
     */
    public void setCustomResolution(boolean customResolution) {
        this.customResolution = customResolution;
    }

    /**
     * Whether the primary viewport provider is allowed update the resolution of the
     * framebuffer.
     */
    public boolean useCustomResolution() {
        return customResolution;
    }

    protected void resetFramebuffer() {
        Window window = client.getWindow();
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetFramebufferSize(window.getHandle(), width, height);

        window.setFramebufferWidth(width[0]);
        window.setFramebufferHeight(height[0]);

        client.onResolutionChanged();
    }

    @Override
    public boolean isActive() {
        return !handles.isEmpty();
    }
}
