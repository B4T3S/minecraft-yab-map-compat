package es.b4t.client;

import me.jfenn.bingo.api.BingoEvents;
import me.jfenn.bingo.api.data.BingoGameStatus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xaero.map.MapProcessor;
import xaero.map.core.XaeroWorldMapCore;
import xaero.map.file.MapSaveLoad;

import java.io.IOException;
import java.nio.file.Path;

public class MinecraftYABMapCompatClient implements ClientModInitializer {
    private BingoGameStatus gameStatus;

    @Override
    public void onInitializeClient() {
        Logger logger = LogManager.getFormatterLogger(es.b4t.MinecraftYABMapCompat.MOD_ID);
//      TODO: Figure out how to intercept status update packages... Otherwise, just check the status every tick, but that's yucky...
//        val GameStatusEvent =
//
//                GameStatusPacket.TYPE
//
//        BingoApi.getGame().getStatus();

        BingoEvents.GAME_STARTING.register((arg) -> {
            if (FabricLoader.getInstance().isModLoaded("xaeroworldmap")) {
                logger.log(Level.INFO, "Found Xaero's worldmap, deleting map!");
                MapProcessor mapProcessor = XaeroWorldMapCore.currentSession.getMapProcessor();
				// Delete all map files
                this.deleteMap(MapSaveLoad.getRootFolder(mapProcessor.getCurrentWorldId()));
				// Force a reload from disk
				mapProcessor.getMapWorld().getCurrentDimension().clear();
            } else {
                logger.log(Level.INFO, "Xaero's worldmap not found, skipping map reset for this mod!");
            }
        });

    }

    private void deleteMap(Path path) {
        if (FileUtils.isDirectory(path.toFile())) {
            try {
                FileUtils.deleteDirectory(path.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}