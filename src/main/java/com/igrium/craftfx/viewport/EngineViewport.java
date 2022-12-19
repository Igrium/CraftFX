package com.igrium.craftfx.viewport;

import java.io.Closeable;
import java.nio.ByteBuffer;

import org.jetbrains.annotations.Nullable;

import com.igrium.craftfx.engine.ViewportProvider;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * <p>
 * Renders a view from the Minecraft engine in a JavaFX scene.
 * </p>
 * <p>
 * This class implements <code>EngineViewportHandle</code>, so it can passed
 * directly to various viewport exporters.</code>
 */
public class EngineViewport extends Region implements EngineViewportHandle, Closeable {
    protected final ImageView imageView = new ImageView();
    private PixelBuffer<ByteBuffer> pixelBuffer;

    private final ObjectProperty<ViewportProvider> viewportProvider = new SimpleObjectProperty<>();

    public EngineViewport() {
        getChildren().add(imageView);
        imageView.setScaleY(-1);
        setFocusTraversable(true);

        viewportProvider.addListener((prop, oldVal, newVal) -> {
            if (oldVal != null) oldVal.removeHandle(this);
            if (newVal != null) newVal.addHandle(this);
        });

        setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
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
    
    /**
     * Set this viewport's viewport provider.
     * @param provider Viewport provider to use.
     */
    public void setViewportProvider(@Nullable ViewportProvider provider) {
        viewportProvider.set(provider);
    }

    /**
     * Get the current viewport provider.
     * @return The viewport provider, or {@code null} if there is none.
     */
    public @Nullable ViewportProvider getViewportProvider() {
        return viewportProvider.get();
    }
    
    public ObjectProperty<ViewportProvider> viewportProviderProperty() {
        return viewportProvider;
    }

    @Override
    public void close() {
        setViewportProvider(null);
    }

}
