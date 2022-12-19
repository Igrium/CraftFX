package com.igrium.craftfx.events;

import net.minecraft.client.render.GameRenderer;

public interface GameRenderContext {
    GameRenderer gameRenderer();
    float tickDelta();
    long startTime();
    boolean tick();

    public static class Impl implements GameRenderContext {
        private final GameRenderer gameRenderer;
        private final float tickDelta;
        private final long startTime;
        private final boolean tick;

        public Impl(GameRenderer gameRenderer, float tickDelta, long startTime, boolean tick) {
            this.gameRenderer = gameRenderer;
            this.tickDelta = tickDelta;
            this.startTime = startTime;
            this.tick = tick;
        }

        @Override
        public GameRenderer gameRenderer() {
            return gameRenderer;
        }

        @Override
        public float tickDelta() {
            return tickDelta;
        }

        @Override
        public long startTime() {
            return startTime;
        }

        @Override
        public boolean tick() {
            return tick;
        }
    }
}
