package com.igrium.craftfx.engine;

import com.igrium.craftfx.viewport.EngineViewportHandle;

public class DummyViewportProvider implements ViewportProvider {

    public static final DummyViewportProvider INSTANCE = new DummyViewportProvider();

    @Override
    public void addHandle(EngineViewportHandle handle) {        
    }

    @Override
    public boolean removeHandle(EngineViewportHandle handle) {
        return false;
    }

    @Override
    public boolean isActive() {
        return false;
    }
    
}
