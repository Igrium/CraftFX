package com.igrium.craftfx.image;

import org.apache.logging.log4j.LogManager;

import com.igrium.craftfx.util.ThreadUtils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;

/**
 * An image view that can display a texture loaded from the Minecraft GL context.
 */
public class TextureView extends ImageView {
    private final ObjectProperty<AbstractTexture> texture = new SimpleObjectProperty<>();

    public TextureView() {
        texture.addListener((prop, oldVal, newVal) -> {
            if (oldVal != newVal) reload();
        });
    }

    /**
     * Create a texture view.
     * @param texture The texture to use.
     */
    public TextureView(AbstractTexture texture) {
        this();
        setTexture(texture);
    }

    /**
     * Create a texture view.
     * @param texture The texture identifier to use.
     */
    public TextureView(Identifier texture) {
        this();
        setTexture(MinecraftClient.getInstance().getTextureManager().getTexture(texture));
    }

    /**
     * Reload the texture.
     */
    public void reload() {
        AbstractTexture texture = this.texture.get();
        if (texture == null) {
            setImage(null);
            return;
        }

        GLTextures.loadTexture(texture).thenAcceptAsync(this::setImage, ThreadUtils::onFXThread).exceptionally(e -> {
            LogManager.getLogger().error("Error loading texture.", e);
            return null;
        });
    }

    public final AbstractTexture getTexture() {
        return texture.get();
    }

    public final void setTexture(AbstractTexture texture) {
        this.texture.set(texture);
    }
    
    /**
     * The Minecraft texture that will be displayed.
     */
    public final Property<AbstractTexture> textureProperty() {
        return texture;
    }
}
