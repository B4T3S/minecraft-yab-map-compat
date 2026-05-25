package es.b4t.client;

import journeymap.client.task.main.DeleteMapTask;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xaero.map.MapProcessor;
import xaero.map.core.XaeroWorldMapCore;
import xaero.map.file.MapSaveLoad;

import java.io.IOException;
import java.nio.file.Path;

public class MapDevourer {
    public static void EradicateCurrentMap() {
        Logger logger = LogManager.getFormatterLogger(es.b4t.MinecraftYABMapCompat.MOD_ID);
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (FabricLoader.getInstance().isModLoaded("xaeroworldmap")) {
            if (player != null) {
                player.sendMessage(Text.literal("Detected Xaero's worldmap at bingo start. Deleting old map data...").withColor(DyeColor.YELLOW.getSignColor()).formatted(Formatting.ITALIC), false);
            }
            logger.log(Level.INFO, "Found Xaero's worldmap, deleting map!");
            MapProcessor mapProcessor = XaeroWorldMapCore.currentSession.getMapProcessor();
            // Delete all map files
            deleteXaerosMapData(MapSaveLoad.getRootFolder(mapProcessor.getCurrentWorldId()));
            // Force a reload from disk
            mapProcessor.getMapWorld().getCurrentDimension().clear();
        } else if (FabricLoader.getInstance().isModLoaded("journeymap")) {
            if (player != null) {
                player.sendMessage(Text.literal("Detected Journeymap at bingo start. Deleting old map data...").withColor(DyeColor.YELLOW.getSignColor()).formatted(Formatting.ITALIC), false);
            }
            DeleteMapTask.queue(true);
        } else {
            logger.error("Didn't find Xaero's Worldmap or Journeymap.... this is awkward");
        }
    }

    private static void deleteXaerosMapData(Path path) {
        if (FileUtils.isDirectory(path.toFile())) {
            try {
                FileUtils.deleteDirectory(path.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
