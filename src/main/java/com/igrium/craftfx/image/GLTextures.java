package com.igrium.craftfx.image;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;

import com.igrium.craftfx.util.ThreadUtils;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import net.minecraft.client.texture.AbstractTexture;

/**
 * A collection of methods that load OpenGL textures into the JavaFX context.
 */
public final class GLTextures {

    private static final PixelFormat<ByteBuffer> PIXEL_FORMAT = PixelFormat.getByteBgraInstance();
    /**
     * Load an AbstractTexture as a JFX image.
     * @param texture The texture to load.
     * @return A future that completes once the texture has been extracted.
     */
    public static CompletableFuture<Image> loadTexture(AbstractTexture texture) {
        return CompletableFuture.supplyAsync(() -> {
            texture.bindTexture();

            // AbstractTexture doesn't save the texture's width/height post-init, so we need
            // to retrieve it from the GPU.
            int width = GlStateManager._getTexLevelParameter(GL11C.GL_TEXTURE_2D, 0, GL11C.GL_TEXTURE_WIDTH);
            int height = GlStateManager._getTexLevelParameter(GL11C.GL_TEXTURE_2D, 0, GL11C.GL_TEXTURE_HEIGHT);

            int size = width * height * 4;
            ByteBuffer buffer = MemoryUtil.memAlloc(size);
            GlStateManager._getTexImage(GlConst.GL_TEXTURE_2D, 0, GL12.GL_BGRA, 5121, MemoryUtil.memAddress(buffer));
            buffer.rewind();
            
            // Copy the buffer into one managed by JavaFX.
            WritableImage image = new WritableImage(width, height);
            image.getPixelWriter().setPixels(0, 0, width, height, PIXEL_FORMAT, buffer, width * 4);
            MemoryUtil.memFree(buffer);

            return image;

        }, ThreadUtils::onRenderThread);
    }

}
