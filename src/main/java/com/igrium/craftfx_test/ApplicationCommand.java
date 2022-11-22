package com.igrium.craftfx_test;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import org.apache.logging.log4j.LogManager;

import com.igrium.craftfx.application.ApplicationManager;
import com.igrium.craftfx.application.ApplicationType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ApplicationCommand {
    private ApplicationCommand() {};

    private static DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(
            id -> Text.literal("No application of type " + id + " exists!"));
    
    private static DynamicCommandExceptionType ERROR = new DynamicCommandExceptionType(
            error -> Text.of("Error launching application: "+error));

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("application").then(
            literal("start").then(
                argument("id", IdentifierArgumentType.identifier()).executes(ApplicationCommand::applicationStart)
            )
        ));

        dispatcher.register(literal("craftfx").then(
            literal("test").executes(context -> {
                try {
                    ApplicationManager.getInstance().launch(CraftFXTest.TEST_APPLICATION);
                } catch (Exception e) {
                    LogManager.getLogger().error("Error loading application.", e);
                    throw ERROR.create(e.getMessage());
                }
                return 1;
            })
        ));
    }

    private static int applicationStart(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        Identifier id = context.getArgument("id", Identifier.class);
        ApplicationType<?> type = ApplicationType.get(id);
        if (type == null) {
            throw NOT_FOUND.create(id);
        }
        
        try {
            ApplicationManager.getInstance().launch(type);
        } catch (Exception e) {
            LogManager.getLogger().error("Error loading application.", e);
            throw ERROR.create(e.getMessage());
        }

        return 1;
    }
}
