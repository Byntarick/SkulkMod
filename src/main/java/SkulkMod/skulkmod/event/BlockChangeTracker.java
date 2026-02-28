package SkulkMod.skulkmod.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;
import java.util.*;
public class BlockChangeTracker {
    private static final Map<String, Map<BlockPos, BlockState>> previousBlockStates = new HashMap<>();
    private static final Map<String, Map<BlockPos, String>> pendingPlacements = new HashMap<>();
    private static int tickCounter = 0;
    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            if (!world.isClient()) {
                String playerName = player.getName().getString();
                String oldBlock = Registries.BLOCK.getId(state.getBlock()).toString();

                WorldBlockHistory.logAction(
                        (ServerWorld) world,
                        pos,
                        playerName,
                        "СЛОМАЛ",
                        oldBlock,
                        "minecraft:air"
                );
            }
            return true;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient() && hand == Hand.MAIN_HAND) {
                BlockPos pos = hitResult.getBlockPos().offset(hitResult.getSide());
                String worldKey = world.getRegistryKey().getValue().toString();

                pendingPlacements.computeIfAbsent(worldKey, k -> new HashMap<>())
                        .put(pos, player.getName().getString());
            }
            return ActionResult.PASS;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                String worldKey = world.getRegistryKey().getValue().toString();


                Map<BlockPos, String> pending = pendingPlacements.get(worldKey);
                if (pending != null && !pending.isEmpty()) {

                    Set<BlockPos> keysToCheck = new HashSet<>(pending.keySet());

                    for (BlockPos pos : keysToCheck) {
                        String playerName = pending.get(pos);

                        if (world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                            BlockState currentState = world.getBlockState(pos);

                            if (!currentState.isAir()) {
                                PlayerEntity player = server.getPlayerManager().getPlayer(playerName);
                                if (player != null) {
                                    WorldBlockHistory.logAction(
                                            world, pos, playerName, "ПОСТАВИЛ",
                                            "minecraft:air",
                                            Registries.BLOCK.getId(currentState.getBlock()).toString()
                                    );
                                }
                                pending.remove(pos);
                            }
                        }

                        if (world.getTime() % 4000 == 0) {
                            pending.remove(pos);
                        }
                    }
                }
                Map<BlockPos, BlockState> worldStates = previousBlockStates.get(worldKey);
                if (worldStates == null) {
                    worldStates = new HashMap<>();
                    previousBlockStates.put(worldKey, worldStates);
                }
                Set<BlockPos> positionsToCheck = new HashSet<>(worldStates.keySet());

                for (BlockPos pos : positionsToCheck) {
                    BlockState oldState = worldStates.get(pos);

                    if (world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        BlockState currentState = world.getBlockState(pos);

                        if (currentState.getBlock() != oldState.getBlock()) {

                            if (!oldState.isAir() && !currentState.isAir()) {
                                PlayerEntity nearestPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);
                                String playerName = nearestPlayer != null ? nearestPlayer.getName().getString() : "UNKNOWN";

                                WorldBlockHistory.logAction(
                                        world, pos, playerName, "ЗАМЕНИЛ",
                                        Registries.BLOCK.getId(oldState.getBlock()).toString(),
                                        Registries.BLOCK.getId(currentState.getBlock()).toString()
                                );
                            }
                            worldStates.put(pos.toImmutable(), currentState);
                        }
                    } else {
                        worldStates.remove(pos);
                    }
                }
                for (BlockPos pos : WorldBlockHistory.getAllHistory(world).keySet()) {
                    if (!worldStates.containsKey(pos) && world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                        worldStates.put(pos, world.getBlockState(pos));
                    }
                }
            }
            tickCounter++;
            if (tickCounter >= 6000) {
                WorldBlockHistory.saveAll(server);
                tickCounter = 0;
            }
        });
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            String worldKey = world.getRegistryKey().getValue().toString();
            WorldBlockHistory.saveWorld(world);
            previousBlockStates.remove(worldKey);
            pendingPlacements.remove(worldKey);
        });
    }
}