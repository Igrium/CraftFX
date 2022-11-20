package com.igrium.craftfx.viewport;

import java.nio.ByteBuffer;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;

/**
 * <p>
 * Renders a view from the Minecraft engine in a JavaFX scene.
 * </p>
 * <p>
 * This class implements <code>EngineViewportHandle</code>, so it can passed
 * directly to various viewport exporters.</code>
 */
public class EngineViewport extends Region implements EngineViewportHandle {
    protected final ImageView imageView = new ImageView();
    private PixelBuffer<ByteBuffer> pixelBuffer;

    public EngineViewport() {
        getChildren().add(imageView);
        imageView.setScaleY(-1);
        setFocusTraversable(true);
    }

    @Override
    public void onAllocate(ByteBuffer buffer, int width, int height) {
        PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteBgraPreInstance();
        pixelBuffer = new PixelBuffer<ByteBuffer>(width, height, buffer, pixelFormat);
        Platform.runLater(() -> imageView.setImage(new WritableImage(pixelBuffer)));
    }

    @Override
    public void update(ByteBuffer buffer) {
        Platform.runLater(() -> pixelBuffer.updateBuffer(pb -> null));
    }

    @Override
    public int getDesiredWidth() {
        return (int) getWidth();
    }

    @Override
    public int getDesiredHeight() {
        return (int) getHeight();
    }
    
}
