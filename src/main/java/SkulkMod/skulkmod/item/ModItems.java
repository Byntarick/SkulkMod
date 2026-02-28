package SkulkMod.skulkmod.item;

import SkulkMod.skulkmod.Skulkmod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.*;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;
import oshi.jna.platform.mac.SystemB;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.item.Items.PHANTOM_MEMBRANE;

public class ModItems {
    public static final Item SCULK_TEMPLATE = registerItem("sculk_template", Item::new);
    public static final Item SCULK_PICKAXE = registerItem("sculk_pickaxe",
            setting -> new Item(setting.pickaxe(ModMaterials.SCULK, 4, -2.5f)));
    public static final Item SCULK_BREAD = registerItem("sculk_bread", settings -> new Item(settings.food(
            new FoodComponent(7, 0.6f, false ))));

    public static final Item SCULK_SWORD = registerItem("sculk_sword",
            setting -> new Item(setting.sword(ModMaterials.SCULK, 6, -2.0f)));

    public static final Item SCULK_SPEAR = registerItem("sculk_spear",
            setting -> new Item(setting.spear(ModMaterials.SCULK, 1.0F, 3.0F,
                    0.15F, 1.2F, 1.5F,
                    0.8F, 1.5F, 1.8F,
                    1.2F)));

    public static final Item SCULK_SHOVEL = registerItem("sculk_shovel",
            setting -> new ShovelItem(ModMaterials.SCULK, 3.5f, -2.8f, setting));

    public static final Item SCULK_AXE = registerItem("sculk_axe",
            setting -> new AxeItem(ModMaterials.SCULK, 8, -2.8f, setting));

    public static final Item SCULK_HOE = registerItem("sculk_hoe",
            setting -> new HoeItem(ModMaterials.SCULK, -1, 2f, setting));

    public static final Item SCULK_HELMET = registerItem("sculk_helmet",
            setting -> new Item(setting.armor(ModMaterials.SCULK_ARMOR, EquipmentType.HELMET)));
    public static final Item SCULK_CHESTPLATE = registerItem("sculk_chestplate",
            setting -> new Item(setting.armor(ModMaterials.SCULK_ARMOR, EquipmentType.CHESTPLATE)));
    public static final Item SCULK_LEGGINGS = registerItem("sculk_leggings",
            setting -> new Item(setting.armor(ModMaterials.SCULK_ARMOR, EquipmentType.LEGGINGS)));
    public static final Item SCULK_BOOTS = registerItem("sculk_boots",
            setting -> new Item(setting.armor(ModMaterials.SCULK_ARMOR, EquipmentType.BOOTS)));

    public static final Item SCULK_HORSE_ARMOR = registerItem("sculk_horse_armor",
            setting -> new Item(setting.horseArmor(ModMaterials.SCULK_ANIMAL_ARMOR)));
    public static final Item SCULK_NAUTILUS_ARMOR = registerItem("sculk_nautilus_armor",
            setting -> new Item(setting.nautilusArmor(ModMaterials.SCULK_ANIMAL_ARMOR)));

    public static final Item SCULK_ELYTRA = register("sculk_elytra", new Item (new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Skulkmod.MOD_ID, "sculk_elytra")))
                    .maxDamage(600)
                    .fireproof()
                    .rarity(Rarity.EPIC)
                    .component(DataComponentTypes.GLIDER, Unit.INSTANCE)
                    .component(
                            DataComponentTypes.EQUIPPABLE,
                            EquippableComponent.builder(EquipmentSlot.CHEST)
                                    .model(registerModel("sculk_elytra"))
                                    .damageOnHurt(false).build()
                    )
                    .repairable(PHANTOM_MEMBRANE)
                    .attributeModifiers(
                            AttributeModifiersComponent.builder()
                                    .add(
                                    EntityAttributes.KNOCKBACK_RESISTANCE,
                                    new EntityAttributeModifier(Identifier.of(Skulkmod.MOD_ID, "elytra.amor"),
                                            0.1F, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.CHEST)
                                    .add(
                                            EntityAttributes.MOVEMENT_SPEED,
                                            new EntityAttributeModifier(
                                                    Identifier.of("sculkelytra", "flight_speed"),
                                                    0.3F,
                                                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                                            ),
                                            AttributeModifierSlot.CHEST
                                    )
                                    .build()

                    )
            )
    );



    static RegistryKey<EquipmentAsset> registerModel(String name) {
        return RegistryKey.of(RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset")), Identifier.of(Skulkmod.MOD_ID, name));
    }
    private static Item register(String name, Item item) {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.addAfter(Items.ELYTRA, ModItems.SCULK_ELYTRA));
        return Registry.register(Registries.ITEM, Identifier.of(Skulkmod.MOD_ID, name), item);
    }



//    public static final Item SCULK_SHIELD = register("sculk_shield");

    private static Item register(String name) {

        Identifier id = Identifier.of(Skulkmod.MOD_ID, name);

        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);

        Item.Settings settings = new Item.Settings()
                .registryKey(key)
                .maxDamage(500);
        Item item = new SculkShield(settings);

        return Registry.register(Registries.ITEM, key, item);
    }

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(Skulkmod.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Skulkmod.MOD_ID, name)))));
    }

    public static void registerModItems (){
    }

}
