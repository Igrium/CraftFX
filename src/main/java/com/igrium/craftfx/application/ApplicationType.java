package com.igrium.craftfx.application;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

/**
 * A specific type of application that can be opened.
 */
public final class ApplicationType<T extends CraftApplication> {

    /**
     * The registry of application types.
     */
    public static final BiMap<Identifier, ApplicationType<?>> REGISTRY = HashBiMap.create();

    public static interface CraftApplicationFactory<T extends CraftApplication> {
        public T create(ApplicationType<T> type, MinecraftClient client);
    }

    private final CraftApplicationFactory<T> factory;

    /**
     * Create an application type.
     * @param factory The application factory.
     */
    public ApplicationType(CraftApplicationFactory<T> factory) {
        this.factory = factory;
    }

    /**
     * Instansiate an instance of this application.
     * @param client Minecraft client.
     * @return The instance.
     */
    public T create(MinecraftClient client) {
        return factory.create(this, client);
    }

    /**
     * Register an application type.
     * @param <S> The application class.
     * @param id The ID to use.
     * @param type The application type.
     * @return <code>type</code>
     */
    public static <S extends CraftApplication> ApplicationType<S> register(
            Identifier id, ApplicationType<S> type) {
        REGISTRY.put(id, type);
        return type;
    }

    /**
     * Get the ID of this application type.
     * @return The ID, or <code>null</code> if this type is unregistered.
     */
    public Identifier getId() {
        return REGISTRY.inverse().get(this);
    }

    /**
     * Get an application type from its ID.
     * 
     * @param id The ID to look for.
     * @return The application type, or <code>null</code> if no application exists
     *         with that ID.
     */
    public static ApplicationType<?> get(Identifier id) {
        return REGISTRY.get(id);
    }
}
