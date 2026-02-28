package SkulkMod.skulkmod;

import SkulkMod.skulkmod.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModGroups {
    public static final RegistryKey<ItemGroup> SCULK_ITEM_GROUP_KEY = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of(Skulkmod.MOD_ID, "sculk_items")
    );

    public static final ItemGroup SCULK_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.SCULK_ELYTRA))
            .displayName(Text.translatable("itemGroup.skulkmod"))
            .build();
    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, SCULK_ITEM_GROUP_KEY, SCULK_ITEM_GROUP);
        ItemGroupEvents.modifyEntriesEvent(SCULK_ITEM_GROUP_KEY).register(itemGroup -> {
            itemGroup.add(ModItems.SCULK_PICKAXE);
            itemGroup.add(ModItems.SCULK_AXE);
            itemGroup.add(ModItems.SCULK_HOE);
            itemGroup.add(ModItems.SCULK_SPEAR);
            itemGroup.add(ModItems.SCULK_SWORD);
            itemGroup.add(ModItems.SCULK_SHOVEL);
            itemGroup.add(ModItems.SCULK_HORSE_ARMOR);
            itemGroup.add(ModItems.SCULK_NAUTILUS_ARMOR);
            itemGroup.add(ModItems.SCULK_BREAD);
            itemGroup.add(ModItems.SCULK_ELYTRA);
            itemGroup.add(ModItems.SCULK_HELMET);
            itemGroup.add(ModItems.SCULK_LEGGINGS);
            itemGroup.add(ModItems.SCULK_CHESTPLATE);
            itemGroup.add(ModItems.SCULK_BOOTS);
            itemGroup.add(ModItems.SCULK_TEMPLATE);
        });
    }
}
