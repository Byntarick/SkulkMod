package SkulkMod.skulkmod.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorldBlockHistory {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Map<String, BlockHistoryData> worldHistories = new ConcurrentHashMap<>();
    private static String getSaveFolderName(ServerWorld world) {
        MinecraftServer server = world.getServer();

        if (server.isDedicated()) {
            return "server";
        } else {
            try {
                Path savesPath = Paths.get("saves");
                if (savesPath.toFile().exists()) {
                    File[] saveDirs = savesPath.toFile().listFiles(File::isDirectory);
                    if (saveDirs != null) {
                        for (File saveDir : saveDirs) {
                            Path levelDat = saveDir.toPath().resolve("level.dat");
                            if (levelDat.toFile().exists()) {
                                return saveDir.getName();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return server.getSaveProperties().getLevelName();
        }
    }

    private static String getWorldUniqueId(ServerWorld world) {
        String saveFolder = getSaveFolderName(world);
        String dimension = world.getRegistryKey().getValue().toString().replace(':', '_');
        return saveFolder + "_" + dimension;
    }

    private static Path getWorldSavePath(ServerWorld world) {
        String dimension = world.getRegistryKey().getValue().toString().replace(':', '_');

        if (world.getServer().isDedicated()) {
            return Paths.get("world/blockhistory/" + dimension + ".json");
        } else {
            String saveFolder = getSaveFolderName(world);
            return Paths.get("saves/" + saveFolder + "/blockhistory/" + dimension + ".json");
        }
    }

    public static BlockHistoryData getWorldData(ServerWorld world) {
        String uniqueId = getWorldUniqueId(world);

        return worldHistories.computeIfAbsent(uniqueId, k -> {
            Path savePath = getWorldSavePath(world);
            System.out.println("📁 Загрузка истории для:");
            System.out.println("   ID: " + uniqueId);
            System.out.println("   Путь: " + savePath);

            BlockHistoryData data = new BlockHistoryData(savePath);
            return data;
        });
    }

    public static void logAction(ServerWorld world, BlockPos pos, String playerName, String action, String oldBlock, String newBlock) {
        BlockHistoryData data = getWorldData(world);
        String time = LocalDateTime.now().format(TIME_FORMATTER);

        data.addAction(pos, playerName, action, oldBlock, newBlock, time);
        data.saveToFile(); // Сохраняем сразу
    }

    public static List<BlockHistoryData.BlockAction> getHistory(ServerWorld world, BlockPos pos) {
        return getWorldData(world).getHistory(pos);
    }

    public static Map<BlockPos, List<BlockHistoryData.BlockAction>> getAllHistory(ServerWorld world) {
        return getWorldData(world).getAllHistory();
    }

    public static void saveWorld(ServerWorld world) {
        String uniqueId = getWorldUniqueId(world);
        BlockHistoryData data = worldHistories.get(uniqueId);
        if (data != null) {
            data.saveToFile();
        }
    }

    public static void saveAll(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            saveWorld(world);
        }
    }
}


