package com.igrium.craftfx_test;

import com.igrium.craftfx.application.ApplicationType;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.util.Identifier;

public class CraftFXTest implements ClientModInitializer {

    public static final ApplicationType<TestApplication> TEST_APPLICATION = ApplicationType
            .register(new Identifier("craftfx_test", "test"), new ApplicationType<>(TestApplication::new));

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(ApplicationCommand::register);
    }
    
}
