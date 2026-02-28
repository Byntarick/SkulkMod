package SkulkMod.skulkmod.event;

import SkulkMod.skulkmod.item.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class SculkCatalistFarm {
    private static final Map<BlockPos, Integer> catalystClicks = new HashMap<>();
    private static final Item[] ALL_SWORDS = new Item[]{
            Items.WOODEN_SWORD,
            Items.STONE_SWORD,
            Items.IRON_SWORD,
            Items.GOLDEN_SWORD,
            Items.DIAMOND_SWORD,
            Items.NETHERITE_SWORD,
            ModItems.SCULK_SWORD
    };
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient()) {
                return ActionResult.PASS;
            }
            BlockPos pos = hitResult.getBlockPos();
            if (!world.getBlockState(pos).isOf(Blocks.SCULK_CATALYST)) return ActionResult.PASS;
            System.out.println("qqqq");

            ItemStack stack = player.getStackInHand(hand);

            if (!isSword(stack)) return ActionResult.PASS;

            handleCatalystClick((ServerWorld) world, pos);

            return ActionResult.SUCCESS;
        });
    }

    private static boolean isSword(ItemStack stack) {
        for (Item sword : ALL_SWORDS) {
            if (stack.isOf(sword)) return true;
        }
        return false;
    }

    private static void handleCatalystClick(ServerWorld world, BlockPos pos) {
        System.out.println("wdwd");
        int clicks = catalystClicks.getOrDefault(pos, 0) + 1;

        if (clicks >= 5) {
            ItemEntity drop = new ItemEntity(
                    world,
                    pos.getX() + 0.5,
                    pos.getY() + 1,
                    pos.getZ() + 0.5,
                    new ItemStack(Items.ECHO_SHARD)
            );
            world.spawnEntity(drop);

            catalystClicks.remove(pos);
        } else {
            catalystClicks.put(pos, clicks);
        }
    }
}
