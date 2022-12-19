package com.igrium.craftfx.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.craftfx.events.GameRenderContext;
import com.igrium.craftfx.events.GameRenderEvents;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    void render(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        GameRenderEvents.START.invoker().onStart(
                new GameRenderContext.Impl((GameRenderer) (Object) this, tickDelta, startTime, tick));
    }
}
