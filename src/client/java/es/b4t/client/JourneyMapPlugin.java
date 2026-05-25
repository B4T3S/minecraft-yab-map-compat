package es.b4t.client;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.client.task.main.DeleteMapTask;
import journeymap.common.Journeymap;
import me.jfenn.bingo.api.BingoEvents;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@journeymap.api.v2.common.JourneyMapPlugin(apiVersion = "2.0.0")
public class JourneyMapPlugin implements IClientPlugin {
    @Override
    public String getModId() {
        return es.b4t.MinecraftYABMapCompat.MOD_ID;
    }

    @Override
    public void initialize(IClientAPI jmClientApi) {
        Logger logger = LogManager.getFormatterLogger(es.b4t.MinecraftYABMapCompat.MOD_ID);
        logger.log(Level.INFO, "Journeymap plugin loading!");

        BingoEvents.GAME_STARTING.register((arg) -> {
            DeleteMapTask.queue(true);
        });

        logger.log(Level.INFO, "Journeymap plugin loaded!");
    }
}
