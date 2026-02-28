package SkulkMod.skulkmod.item;

import SkulkMod.skulkmod.Skulkmod;
import com.google.common.collect.Maps;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.Map;

import static net.minecraft.item.equipment.EquipmentAssetKeys.register;


public class ModMaterials {
    public static final ToolMaterial SCULK = new ToolMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            2200,   // durability
            11.0F,   // mining speed
            3.0F,   // attack damage
            15,     // enchantability
            TagKey.of(
                    Registries.ITEM.getKey(),
                    Identifier.of(Skulkmod.MOD_ID, "skulk_repair")
            )
    );
    RegistryKey<? extends Registry<EquipmentAsset>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset"));
 private final static RegistryKey<EquipmentAsset> NOTHING = register("sculk");

    public static final ArmorMaterial SCULK_ARMOR = new ArmorMaterial(60, createDefenseMap(5, 8, 10, 5, 22),
            25, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 5.0F, 0.3F, TagKey.of(
            Registries.ITEM.getKey(),
            Identifier.of(Skulkmod.MOD_ID, "skulk_repair")
    ),
            NOTHING);
    public static final ArmorMaterial SCULK_ANIMAL_ARMOR = new ArmorMaterial(60, createDefenseMap(0, 0, 0, 0, 22),
            25, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 5.0F, 0.3F, TagKey.of(
            Registries.ITEM.getKey(),
            Identifier.of(Skulkmod.MOD_ID, "skulk_repair")
    ), NOTHING);
    static Map<EquipmentType, Integer> createDefenseMap(int bootsDefense, int leggingsDefense, int chestplateDefense, int helmetDefense, int bodyDefense) {
        return Maps.newEnumMap(Map.of(EquipmentType.BOOTS, bootsDefense, EquipmentType.LEGGINGS, leggingsDefense, EquipmentType.CHESTPLATE, chestplateDefense, EquipmentType.HELMET, helmetDefense, EquipmentType.BODY, bodyDefense));
    }
}
