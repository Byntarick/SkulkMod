package SkulkMod.skulkmod.event;
import SkulkMod.skulkmod.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SculkRepairEvent {
    private static int time = 1;


    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.getPlayerManager().getPlayerList().forEach(SculkRepairEvent::tickPlayer);
        });
    }

    private static void tickPlayer(PlayerEntity player) {
        time++;
       if(time % 5 == 0) {
           int count = 0;
        World world = player.getEntityWorld();
        int R = 3;

        BlockPos center = player.getBlockPos();

        for (int x = -R; x <= R; x++) {
            for (int y = -R; y <= R; y++) {
                for (int z = -R; z <= R; z++) {
                    BlockPos pos = center.add(x, y, z);
                    if (world.getBlockState(pos).isOf(Blocks.SCULK)) {
                        count++;
                    }
                }
            }
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getEquippedStack(slot);
            if (!world.isClient() && world instanceof ServerWorld serverWorld) {
                if (!hasMending(serverWorld, stack)) continue;
                    if (stack.isEmpty()) continue;
                if (stack.isDamageable() && isSculk(stack)) {
                    int damage = stack.getDamage();
                    if (damage > 0) stack.setDamage(damage - (count/8));
                }
            }
        }
    }
    if (time == 6){
        time = 1;
    }

    }
    private static boolean isSculk(ItemStack stack){
        if (stack.isOf(ModItems.SCULK_PICKAXE) || stack.isOf(ModItems.SCULK_HOE) || stack.isOf(ModItems.SCULK_AXE)|| stack.isOf(ModItems.SCULK_SHOVEL)
                || stack.isOf(ModItems.SCULK_SWORD) || stack.isOf(ModItems.SCULK_BOOTS)|| stack.isOf(ModItems.SCULK_CHESTPLATE)
                || stack.isOf(ModItems.SCULK_HELMET) || stack.isOf(ModItems.SCULK_LEGGINGS) || stack.isOf(ModItems.SCULK_ELYTRA) || stack.isOf(ModItems.SCULK_SPEAR)
        ){
            return true;
        } else return false;
    }
    public static boolean hasMending(ServerWorld world, ItemStack stack) {

        ItemEnchantmentsComponent enchants =
                stack.get(DataComponentTypes.ENCHANTMENTS);

        if (enchants == null) return false;

        RegistryEntry<?> mending = world
                .getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT)
                .getOrThrow(Enchantments.MENDING);

        return enchants.getEnchantments().contains(mending);
    }

}