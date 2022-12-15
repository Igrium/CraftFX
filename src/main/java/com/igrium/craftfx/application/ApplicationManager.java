package com.igrium.craftfx.application;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.igrium.craftfx.CraftFX;

import javafx.application.Platform;
import javafx.stage.Stage;
import net.minecraft.client.MinecraftClient;

public final class ApplicationManager {
    
    /**
     * Get the active <code>ApplicationManager</code> instance. Shortcut for
     * <code>CraftFX.getInstance().getApplicationManager();</code>
     * 
     * @return Active instance.
     */
    public static ApplicationManager getInstance() {
        return CraftFX.getInstance().getApplicationManager();
    }

    private final CraftFX mod;
    private final Map<ApplicationType<?>, CraftApplication> applications = new HashMap<>();

    private boolean pauseCache;

    public ApplicationManager(CraftFX mod) {
        this.mod = mod;
    }

    /**
     * Get the active <code>CraftFX</code> instance.
     * @return Mod instance.
     */
    public CraftFX getMod() {
        return mod;
    }
    
    /**
     * Get an application instance by its type.
     * @param type The application type to use.
     * @return The application instance, if it exists.
     */
    public Optional<CraftApplication> getApplication(ApplicationType<?> type) {
        return Optional.ofNullable(applications.get(type));
    }

    /**
     * Launch a new application. Blocks until JavaFX is initialized and the
     * application has started.
     * 
     * @param type The application type.
     * @return The application instance.
     * @throws IllegalArgumentException If the application type is not registered.
     * @throws IllegalStateException    If the application is already running.
     * @see #launch(ApplicationType, Consumer)
     */
    public final <T extends CraftApplication> T launch(ApplicationType<T> type) {
        return launch(type, null);
    }

    /**
     * Launch a new application. Blocks until JavaFX is initialized and the
     * application has started.
     * 
     * @param type        The application type.
     * @param initializer A function that's called on the instantiated application
     *                    <i>before</i> it's passed to JavaFX.
     * @return The application instance.
     * @throws IllegalArgumentException If the application type is not registered.
     * @throws IllegalStateException    If the application is already running.
     * @see #launch(ApplicationType)
     */
    public <T extends CraftApplication> T launch(ApplicationType<T> type, @Nullable Consumer<T> initializer) throws IllegalArgumentException, IllegalStateException {
        if (!ApplicationType.REGISTRY.inverse().containsKey(type)) {
            throw new IllegalArgumentException("Application type is not registered!");
        }

        if (applications.containsKey(type)) {
            throw new IllegalStateException("Application "+type.getId()+" is already running!");
        }

        MinecraftClient client = MinecraftClient.getInstance();

        T application = type.create(client);
        if (initializer != null) initializer.accept(application);

        applications.put(type, application);
        
        // Stop Minecraft from pausing
        pauseCache = client.options.pauseOnLostFocus;
        client.options.pauseOnLostFocus = false;

        application.setup();
        
        RootApplication root = RootApplication.launchJFX();
        Platform.runLater(() -> {
            Stage stage = new Stage();

            stage.setOnHidden(e -> {
                try {
                    application.onClosed();
                } catch (Exception e1) {
                    CraftFX.LOGGER.error("Error closing application "+type.getId(), e);
                }
                client.options.pauseOnLostFocus = pauseCache;
                applications.remove(type);
            });

            application.init(stage, root);
            try {
                application.start(stage, root);
            } catch (Exception e) {
                CraftFX.LOGGER.error("Error launching application "+type.getId(), e);
                applications.remove(type);
            }
        });
        
        return application;
    }

    /**
     * Check if a particular application is running.
     * @param type The application type.
     * @return Is it running?
     */
    public boolean isRunning(ApplicationType<?> type) {
        return applications.containsKey(type);
    }

    /**
     * Get the running instance of an application of a given type.
     * @param type The application type.
     * @return The instance, or an empty optional if it is not running.
     */
    @SuppressWarnings("unchecked")
    public <T extends CraftApplication> Optional<T> getAppInstance(ApplicationType<T> type) {
        return Optional.ofNullable((T) applications.get(type));
    }
}
