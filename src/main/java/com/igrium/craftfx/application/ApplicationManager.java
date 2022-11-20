package com.igrium.craftfx.application;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.igrium.craftfx.CraftFX;
import com.igrium.craftfx.engine.PrimaryViewportProvider;

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
     * Launch a new application. Blocks until JavaFX is initialized and the application has started.
     * @param type The application type. 
     * @return The application instance.
     * @throws IllegalArgumentException If the application type is not registered.
     * @throws IllegalStateException If the application is already running.
     */
    public CraftApplication launch(ApplicationType<?> type) throws IllegalArgumentException, IllegalStateException {
        if (!ApplicationType.REGISTRY.inverse().containsKey(type)) {
            throw new IllegalArgumentException("Application type is not registered!");
        }

        if (applications.containsKey(type)) {
            throw new IllegalStateException("Application "+type.getId()+" is already running!");
        }

        CraftApplication application = type.create(MinecraftClient.getInstance());
        applications.put(type, application);

        RootApplication root = RootApplication.launchJFX();
        Platform.runLater(() -> {
            Stage stage = new Stage();

            stage.setOnHidden(e -> {
                application.onClosed();
                applications.remove(type);
                PrimaryViewportProvider.getInstance().setHandle(null);
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
}
