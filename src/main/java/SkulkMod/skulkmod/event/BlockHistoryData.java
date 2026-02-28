package SkulkMod.skulkmod.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockHistoryData extends PersistentState {
    private final Map<BlockPos, List<BlockAction>> blockHistory = new ConcurrentHashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Path savePath;

    public BlockHistoryData(Path savePath) {
        this.savePath = savePath;
        loadFromFile();
    }

    public void saveToFile() {
        try {
            Files.createDirectories(savePath.getParent());

            List<SerializableAction> actions = new ArrayList<>();
            for (Map.Entry<BlockPos, List<BlockAction>> entry : blockHistory.entrySet()) {
                BlockPos pos = entry.getKey();
                for (BlockAction action : entry.getValue()) {
                    actions.add(new SerializableAction(
                            pos.getX(), pos.getY(), pos.getZ(),
                            action.player, action.action,
                            action.oldBlock, action.newBlock, action.time
                    ));
                }
            }

            String json = GSON.toJson(actions);
            Files.writeString(savePath, json);

        } catch (IOException e) {
            System.err.println("Ошибка сохранения: " + savePath);
            e.printStackTrace();
        }
    }

    public void loadFromFile() {
        try {
            if (!Files.exists(savePath)) {
                return;
            }

            String json = Files.readString(savePath);
            Type listType = new TypeToken<ArrayList<SerializableAction>>(){}.getType();
            List<SerializableAction> actions = GSON.fromJson(json, listType);

            blockHistory.clear();

            for (SerializableAction action : actions) {
                BlockPos pos = new BlockPos(action.x, action.y, action.z);
                BlockAction ba = new BlockAction(
                        pos, action.player, action.action,
                        action.oldBlock, action.newBlock, action.time
                );
                blockHistory.computeIfAbsent(pos, k -> new ArrayList<>()).add(ba);
            }

        } catch (IOException e) {
            System.err.println("Ошибка загрузки: " + savePath);
            e.printStackTrace();
        }
    }

    public void addAction(BlockPos pos, String player, String action, String oldBlock, String newBlock, String time) {
        BlockAction ba = new BlockAction(pos, player, action, oldBlock, newBlock, time);
        blockHistory.computeIfAbsent(pos.toImmutable(), k -> new ArrayList<>()).add(ba);
    }

    public List<BlockAction> getHistory(BlockPos pos) {
        return blockHistory.getOrDefault(pos, new ArrayList<>());
    }

    public Map<BlockPos, List<BlockAction>> getAllHistory() {
        return blockHistory;
    }

    private static class SerializableAction {
        int x, y, z;
        String player, action, oldBlock, newBlock, time;

        SerializableAction(int x, int y, int z, String player, String action,
                           String oldBlock, String newBlock, String time) {
            this.x = x; this.y = y; this.z = z;
            this.player = player; this.action = action;
            this.oldBlock = oldBlock; this.newBlock = newBlock;
            this.time = time;
        }
    }

    public static class BlockAction {
        public final BlockPos pos;
        public final String player;
        public final String action;
        public final String oldBlock;
        public final String newBlock;
        public final String time;

        public BlockAction(BlockPos pos, String player, String action, String oldBlock, String newBlock, String time) {
            this.pos = pos;
            this.player = player;
            this.action = action;
            this.oldBlock = oldBlock;
            this.newBlock = newBlock;
            this.time = time;
        }
    }
}