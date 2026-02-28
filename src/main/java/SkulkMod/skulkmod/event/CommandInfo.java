package SkulkMod.skulkmod.event;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.*;
public class CommandInfo {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher);
        });
    }
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("blockinfo")
                .then(literal("history")
                        .then(argument("x", IntegerArgumentType.integer())
                                .then(argument("y", IntegerArgumentType.integer())
                                        .then(argument("z", IntegerArgumentType.integer())
                                                .executes(context -> showHistory(
                                                        context,
                                                        IntegerArgumentType.getInteger(context, "x"),
                                                        IntegerArgumentType.getInteger(context, "y"),
                                                        IntegerArgumentType.getInteger(context, "z")
                                                ))
                                        )
                                )
                        )
                )
                .then(literal("stats")
                        .executes(CommandInfo::showStats)
                )
                .then(literal("save")
                        .executes(CommandInfo::forceSave)
                )
                .then(literal("worlds")
                        .executes(CommandInfo::showWorlds)
                )
        );
    }

    private static int showHistory(CommandContext<ServerCommandSource> context, int x, int y, int z) {
        ServerWorld world = context.getSource().getWorld();
        BlockPos pos = new BlockPos(x, y, z);
        List<BlockHistoryData.BlockAction> history = WorldBlockHistory.getHistory(world, pos);

        String saveName = world.getServer().getSaveProperties().getLevelName();
        String dimension = world.getRegistryKey().getValue().toString();

        if (history.isEmpty()) {
            context.getSource().sendFeedback(() ->
                            Text.literal("Нет записей для блока")
                                    .formatted(Formatting.GRAY),
                    false
            );
            context.getSource().sendFeedback(() ->
                            Text.literal("   Сохранение: " + saveName)
                                    .formatted(Formatting.AQUA),
                    false
            );
            context.getSource().sendFeedback(() ->
                            Text.literal("   Измерение: " + dimension)
                                    .formatted(Formatting.AQUA),
                    false
            );
            context.getSource().sendFeedback(() ->
                            Text.literal("   Координаты: " + x + " " + y + " " + z)
                                    .formatted(Formatting.YELLOW),
                    false
            );
            return 0;
        }

        context.getSource().sendFeedback(() ->
                        Text.literal("═══════════════════════════════════════")
                                .formatted(Formatting.GOLD),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal("ИСТОРИЯ БЛОКА")
                                .formatted(Formatting.GOLD, Formatting.BOLD),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal("   Сохранение: " + saveName)
                                .formatted(Formatting.AQUA),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal("   Измерение: " + dimension)
                                .formatted(Formatting.AQUA),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal("   Координаты: " + x + " " + y + " " + z)
                                .formatted(Formatting.YELLOW),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal("═══════════════════════════════════════")
                                .formatted(Formatting.GOLD),
                false
        );

        for (int i = 0; i < history.size(); i++) {
            BlockHistoryData.BlockAction action = history.get(i);
            final int displayIndex = i + 1;
            final BlockHistoryData.BlockAction finalAction = action;

            context.getSource().sendFeedback(() -> {
                Text prefix = Text.literal(String.format("%2d. ", displayIndex)).formatted(Formatting.DARK_GRAY);
                Text time = Text.literal("[" + finalAction.time + "] ").formatted(Formatting.DARK_GREEN);
                Text player = Text.literal(finalAction.player).formatted(Formatting.AQUA);

                Text actionText;
                if (finalAction.action.equals("СЛОМАЛ")) {
                    actionText = Text.literal(" СЛОМАЛ ").formatted(Formatting.RED);
                } else if (finalAction.action.equals("ПОСТАВИЛ")) {
                    actionText = Text.literal(" ПОСТАВИЛ ").formatted(Formatting.GREEN);
                } else {
                    actionText = Text.literal(" " + finalAction.action + " ").formatted(Formatting.YELLOW);
                }

                Text blocks = Text.literal(finalAction.oldBlock + " → " + finalAction.newBlock).formatted(Formatting.WHITE);

                return Text.literal("").append(prefix).append(time).append(player).append(actionText).append(blocks);
            }, false);
        }

        context.getSource().sendFeedback(() ->
                        Text.literal("═══════════════════════════════════════")
                                .formatted(Formatting.GOLD),
                false
        );

        final int totalSize = history.size();
        context.getSource().sendFeedback(() ->
                        Text.literal("Всего записей: " + totalSize)
                                .formatted(Formatting.GRAY),
                false
        );

        return 1;
    }

    private static int showStats(CommandContext<ServerCommandSource> context) {
        ServerWorld world = context.getSource().getWorld();
        Map<BlockPos, List<BlockHistoryData.BlockAction>> allHistory = WorldBlockHistory.getAllHistory(world);

        int totalBlocks = allHistory.size();

        int actionCount = 0;
        for (List<BlockHistoryData.BlockAction> actions : allHistory.values()) {
            actionCount += actions.size();
        }
        final int totalActions = actionCount;

        BlockPos mostActive = null;
        int maxActions = 0;

        for (Map.Entry<BlockPos, List<BlockHistoryData.BlockAction>> entry : allHistory.entrySet()) {
            int size = entry.getValue().size();
            if (size > maxActions) {
                maxActions = size;
                mostActive = entry.getKey();
            }
        }

        String saveName = world.getServer().getSaveProperties().getLevelName();
        String dimension = world.getRegistryKey().getValue().toString();

        context.getSource().sendFeedback(() ->
                        Text.literal("═══════════════════════════════════════")
                                .formatted(Formatting.GOLD),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal("СТАТИСТИКА")
                                .formatted(Formatting.GOLD, Formatting.BOLD),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal("   Сохранение: " + saveName)
                                .formatted(Formatting.AQUA),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal("   Измерение: " + dimension)
                                .formatted(Formatting.AQUA),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal("═══════════════════════════════════════")
                                .formatted(Formatting.GOLD),
                false
        );

        final int finalTotalBlocks = totalBlocks;
        context.getSource().sendFeedback(() ->
                        Text.literal("Отслеживается блоков: " + finalTotalBlocks)
                                .formatted(Formatting.WHITE),
                false
        );

        context.getSource().sendFeedback(() ->
                        Text.literal("Всего действий: " + totalActions)
                                .formatted(Formatting.WHITE),
                false
        );

        if (mostActive != null) {
            BlockPos finalMostActive = mostActive;
            int finalMaxActions = maxActions;
            context.getSource().sendFeedback(() ->
                            Text.literal("Самый активный блок: ["
                                            + finalMostActive.getX() + " "
                                            + finalMostActive.getY() + " "
                                            + finalMostActive.getZ() + "] ("
                                            + finalMaxActions + " действий)")
                                    .formatted(Formatting.YELLOW),
                    false
            );
        }

        return 1;
    }

    private static int showWorlds(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld currentWorld = source.getWorld();
        MinecraftServer server = source.getServer();

        source.sendFeedback(() -> Text.literal("══════════════════════════════").formatted(Formatting.GOLD), false);
        source.sendFeedback(() -> Text.literal("СОХРАНЕНИЯ").formatted(Formatting.GOLD, Formatting.BOLD), false);
        source.sendFeedback(() -> Text.literal("══════════════════════════════").formatted(Formatting.GOLD), false);

        // Просто показываем все миры на сервере
        for (ServerWorld world : server.getWorlds()) {
            String worldName = world.getRegistryKey().getValue().toString();
            Text worldText = Text.literal("• " + worldName)
                    .formatted(world == currentWorld ? Formatting.GREEN : Formatting.WHITE);
            source.sendFeedback(() -> worldText, false);
        }

        source.sendFeedback(() -> Text.literal("══════════════════════════════").formatted(Formatting.GOLD), false);
        return 1;
    }
    private static int forceSave(CommandContext<ServerCommandSource> context) {
        WorldBlockHistory.saveAll(context.getSource().getServer());

        context.getSource().sendFeedback(() ->
                        Text.literal("История всех миров принудительно сохранена")
                                .formatted(Formatting.GREEN),
                false
        );

        return 1;
    }
}