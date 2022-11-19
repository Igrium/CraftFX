package com.igrium.craftfx.application;

import javafx.application.Application;
import javafx.stage.Stage;
import net.minecraft.client.MinecraftClient;

/**
 * An application class from which CraftFX applications extend. Used instead of
 * {@link Application} because of the need to support multiple instances per JVM
 * runtime.
 */
public abstract class CraftApplication {
    private Stage stage;
    private Application parent;

    private final ApplicationType<?> type;
    private final MinecraftClient client;

    public CraftApplication(ApplicationType<?> type, MinecraftClient client) {
        this.type = type;
        this.client = client;
    }

    /**
     * The primary entrypoint for your application. It will always be called on the
     * JavaFX thread, after JavaFX has been initialized and the application is ready
     * to start.
     * 
     * @param primaryStage The stage for this application.
     * @param parent       The global JavaFX application that this application is a
     *                     part of.
     * @throws Exception If something goes wrong while starting the application.
     */
    public abstract void start(Stage primaryStage, Application parent) throws Exception;

    /**
     * Pre-initialize this application. <b>Internal use only</b>
     */
    public final void init(Stage primaryStage, Application parent) {
        this.stage = primaryStage;
        this.parent = parent;
    }

    /**
     * Get this application's type.
     * @return The application type.
     */
    public final ApplicationType<?> getType() {
        return type;
    }

    /**
     * Get the Minecraft client.
     * @return The Minecraft client.
     */
    public MinecraftClient getClient() {
        return client;
    }

    /**
     * Get the primary stage of this application.
     * @return Primary stage.
     */
    public final Stage getStage() {
        return stage;
    }

    /**
     * Get the global JavaFX application that this application is a part of.
     * @return JavaFX application.
     */
    public final Application getParent() {
        return parent;
    }

    /**
     * Attempt to stop this application.
     */
    public void stop() {
        getStage().close();
    }
}
