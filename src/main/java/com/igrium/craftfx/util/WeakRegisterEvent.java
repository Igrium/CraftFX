package com.igrium.craftfx.util;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import net.fabricmc.fabric.api.event.Event;
/**
 * A wrapper around {@link Event} that doesn't store strong references to it's
 * listeners. When a listener is registered to this wrapper, if there are no
 * other strong references, it is still eligible for garbage collection.
 * 
 * @param <T> The listener type.
 * @see Event
 */
public class WeakRegisterEvent<T> {

    protected final Set<T> listeners = Collections.synchronizedSet(
            Collections.newSetFromMap(
                    new WeakHashMap<>()));
    
    protected final Event<T> baseEvent;
    private final T listener;

    /**
     * Create a weak register event.
     * 
     * @param <P>             A type that condenses all the arguments for an event
     *                        call into one object.
     * @param event           The base event.
     * @param listenerFactory Creates a valid event listener that calls the provided
     *                        consumer with all the relevent arguments.
     * @param invoker         Invokes all the provided listeners with the provided
     *                        arguments.
     */
    public <P> WeakRegisterEvent(Event<T> event, Class<P> paramType, Function<Consumer<P>, T> listenerFactory,
            BiConsumer<T, P> invoker) {
        this.baseEvent = event;
        this.listener = listenerFactory.apply(params -> {
            listeners.forEach(l -> invoker.accept(l, params));
        });
        
        baseEvent.register(listener);
    }

    public void register(T listener) {
        listeners.add(listener);
    }

    public boolean unregister(T listener) {
        return listeners.remove(listener);
    }

}
