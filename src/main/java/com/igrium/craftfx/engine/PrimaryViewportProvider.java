package com.igrium.craftfx.engine;

import java.nio.ByteBuffer;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;

import com.igrium.craftfx.viewport.EngineViewportHandle;
import com.mojang.blaze3d.systems.RenderSystem;

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

    protected EngineViewportHandle handle;
    MinecraftClient client = MinecraftClient.getInstance();

    public synchronized void update() {
        RenderSystem.assertOnRenderThread();
        if (handle == null) {
            if (buffer != null) {
                MemoryUtil.memFree(buffer);
                buffer = null;
            }
            return;
        };

        Framebuffer frameBuffer = MinecraftClient.getInstance().getFramebuffer();
        int x = frameBuffer.textureWidth; int y = frameBuffer.textureHeight;
        
        // Reallocate
        if (buffer == null || x != currentX || y != currentY) {
            if (buffer != null) {
                MemoryUtil.memFree(buffer);
            }

            currentX = x;
            currentY = y;
            buffer = MemoryUtil.memAlloc(x * y  * 4);

            handle.onAllocate(buffer, x, y);
        }

        RenderSystem.bindTexture(frameBuffer.getColorAttachment());
        RenderSystem.readPixels(0, 0, x, y, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);
        buffer.rewind();

        handle.update(buffer);
    }

    public synchronized void pretick() {
        if (handle == null) return;
        int x = handle.getDesiredWidth();
        int y = handle.getDesiredHeight();

        if (x <= 0 || y <= 0) return;

        Framebuffer fb = client.getFramebuffer();
        if (fb.viewportWidth != x || fb.viewportHeight != y) {
            fb.resize(x, y, false);

            Window window = client.getWindow();
            window.setFramebufferWidth(x);
            window.setFramebufferHeight(y);

            client.gameRenderer.onResized(x, y);
        }
    }

    @Override
    public void setHandle(@Nullable EngineViewportHandle handle) {
        if (this.handle == handle) return;
        if (this.handle != null) this.handle.onRemoved(this);

        this.handle = handle;
        if (handle != null) handle.onSet(this);
    }

    @Override
    public @Nullable EngineViewportHandle getHandle() {
        return handle;
    }

    @Override
    public boolean isActive() {
        return handle != null;
    }
}
