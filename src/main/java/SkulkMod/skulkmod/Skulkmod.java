package SkulkMod.skulkmod;


import SkulkMod.skulkmod.event.*;
import SkulkMod.skulkmod.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;




public class Skulkmod implements ModInitializer {
    public static final String MOD_ID = "skulkmod";

    @Override
    public void onInitialize() {
        ModItems.registerModItems();
        ModGroups.registerItemGroups();
        SculkRepairEvent.register();
    }
}