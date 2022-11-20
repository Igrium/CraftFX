package com.igrium.craftfx.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.craftfx.engine.PrimaryViewportProvider;

import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;beginWrite(Z)V", shift = At.Shift.AFTER))
    private void afterRender(CallbackInfo ci) {
        PrimaryViewportProvider.getInstance().update();
    }
}
