package es.b4t.client;

import journeymap.client.task.main.DeleteMapTask;
import journeymap.client.waypoint.WaypointHandler;
import journeymap.common.nbt.waypoint.WaypointDAO;
import journeymap.common.waypoint.WaypointImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xaero.common.core.XaeroMinimapCore;
import xaero.hud.minimap.waypoint.WaypointSession;
import xaero.hud.minimap.waypoint.render.WaypointDeleter;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.map.MapProcessor;
import xaero.map.core.XaeroWorldMapCore;
import xaero.map.file.MapSaveLoad;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class MapDevourer {

    public static void EradicateCurrentMap() {
        EradicateCurrentMap(false);
    }

    public static void EradicateCurrentMap(boolean suppressChatMessage) {
        Logger logger = LogManager.getFormatterLogger(es.b4t.MinecraftYABMapCompat.MOD_ID);
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        boolean xaerosWorldmapLoaded = FabricLoader.getInstance().isModLoaded("xaeroworldmap");
        boolean xaerosMinimapLoaded = FabricLoader.getInstance().isModLoaded("xaerominimap");
        boolean journeymapLoaded = FabricLoader.getInstance().isModLoaded("journeymap");

        if (xaerosWorldmapLoaded) {
            if (!suppressChatMessage) yellAtPlayer(player, "Xaero's worldmap");
            logger.log(Level.INFO, "Found Xaero's worldmap, deleting map!");
            MapProcessor mapProcessor = XaeroWorldMapCore.currentSession.getMapProcessor();

            // Delete all map files
            deleteXaerosMapData(logger, MapSaveLoad.getRootFolder(mapProcessor.getCurrentWorldId()));
            // Force a reload from disk
            mapProcessor.getMapWorld().getCurrentDimension().clear();
        }

        if (xaerosMinimapLoaded) {
            if (!suppressChatMessage) yellAtPlayer(player, "Xaero's minimap");
            logger.log(Level.INFO, "Found Xaero's minimap, deleting waypoints!");
            deleteXaerosWaypoints();
        }

        if (journeymapLoaded) {
            if (!suppressChatMessage) yellAtPlayer(player, "Journeymap");
            DeleteMapTask.queue(true);
            deleteJourneymapWaypoints();
        }
    }

    private static void deleteXaerosMapData(Logger logger, Path path) {
        if (FileUtils.isDirectory(path.toFile())) {
            try {
                logger.info(String.format("Deleting Xaero's folder at %s", path));
                FileUtils.deleteDirectory(path.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void deleteXaerosWaypoints() {
        WaypointSession waypointSession = XaeroMinimapCore.currentSession.getMinimapProcessor().getSession().getWaypointSession();
        Iterable<WaypointSet> waypointSets = waypointSession.getSession().getWorldManager().getCurrentWorld().getIterableWaypointSets();
        WaypointDeleter deleter = waypointSession.getDeleter();
        deleter.begin();
        waypointSets.forEach(waypointSet -> {
            waypointSet.getWaypoints().forEach(deleter::add);
        });
    }

    private static void deleteJourneymapWaypoints() {
        WaypointDAO dao = WaypointHandler.getInstance().getDao();
        Map<String, WaypointImpl> waypoints = dao.getWaypoints();

        waypoints.forEach((wpName, wpImpl) -> {
            dao.deleteWaypoint(wpImpl);
        });

        dao.save(true);
    }

    private static void yellAtPlayer(ClientPlayerEntity player, String mapType) {
        if (player != null) {
            player.sendMessage(
                    Text.literal("Detected ").withColor(DyeColor.YELLOW.getSignColor()).formatted(Formatting.ITALIC)
                            .append(Text.literal(mapType).withColor(DyeColor.CYAN.getSignColor()).formatted(Formatting.BOLD).formatted(Formatting.ITALIC))
                            .append(Text.literal(" at bingo start. Deleting old map data...").withColor(DyeColor.YELLOW.getSignColor()).formatted(Formatting.ITALIC))
            , false);
        }
    }
}
