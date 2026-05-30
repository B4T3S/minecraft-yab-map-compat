package es.b4t.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

public class MinecraftYABMapCompatClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("clearmap").executes(context -> {
                MapDevourer.EradicateCurrentMap(true);

                return 1;
            }));
        }));
    }
}