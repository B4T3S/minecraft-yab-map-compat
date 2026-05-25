package es.b4t.client;

import me.jfenn.bingo.api.BingoApi;
import me.jfenn.bingo.api.data.BingoGameStatus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MinecraftYABMapCompatClient implements ClientModInitializer {
    private BingoGameStatus gameStatus;

    @Override
    public void onInitializeClient() {

        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            var game = BingoApi.getGame();

            if (game != null && this.gameStatus != game.getStatus()) {
                this.gameStatus = game.getStatus();

                if (this.gameStatus == BingoGameStatus.STARTING) {
                    // The gamestate just changed to "Starting". We can start deleting the old map now.
                    MapDevourer.EradicateCurrentMap();
                }
            }
        });

    }
}