package net.brodino.summonmounts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.brodino.summonmounts.commands.SpawnRandomHorseCommand;

public class Summonmounts implements ModInitializer {

    public static final String MOD_ID = "summonmounts";
    public static final Logger LOGGER = LoggerFactory.getLogger(Summonmounts.MOD_ID);
    public static Config CONFIG = Config.createAndLoad();
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing SummonMounts");
        
        // Register event handlers
        EventHandlers.register();
        
        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SpawnRandomHorseCommand.register(dispatcher);
        });
        
        // Register item use callbacks
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            EventHandlers.itemUsedOnAnEntity(player, world, hand, entity, hitResult);
            return ActionResult.PASS;
        });
        
        UseItemCallback.EVENT.register((player, world, hand) -> {
            return EventHandlers.onItemUse(player, world, hand);
        });
    }

    public void onReload() {
        LOGGER.info("Reloading SummonMounts config");
        Summonmounts.CONFIG = Config.createAndLoad();
    }
}
