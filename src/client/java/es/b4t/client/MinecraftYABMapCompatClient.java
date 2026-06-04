package es.b4t.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class MinecraftYABMapCompatClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("clearmap").executes(context -> {
                MapDevourer.EradicateCurrentMap(true);

                return 1;
            }));
        }));
    }
}