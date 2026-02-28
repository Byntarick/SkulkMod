package SkulkMod.skulkmod.item;

import SkulkMod.skulkmod.Skulkmod;
import net.minecraft.component.Component;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;



import java.util.List;
import java.util.Optional;

public class SculkShield extends Item {

    public static final String MOD_ID = "skulkmod";

    public SculkShield(Settings settings) {
        super(settings.component(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT)
                .component(DataComponentTypes.BLOCKS_ATTACKS,
                        new BlocksAttacksComponent(
                                0.25F, // задержка перед блоком
                                1.0F,  // множитель кулдауна
                                List.of(
                                        new BlocksAttacksComponent.DamageReduction(
                                                90.0F,
                                                Optional.empty(),
                                                0.0F,
                                                1.0F
                                        )
                                ),
                                new BlocksAttacksComponent.ItemDamage(
                                        3.0F,   // урон по щиту
                                        1.0F,
                                        1.0F
                                ),
                                Optional.of(DamageTypeTags.BYPASSES_SHIELD),
                                Optional.of(SoundEvents.ITEM_SHIELD_BLOCK),
                                Optional.of(SoundEvents.ITEM_SHIELD_BREAK)
                        )
                )
                .component(DataComponentTypes.BREAK_SOUND, SoundEvents.ITEM_SHIELD_BREAK)
        );
    }
}