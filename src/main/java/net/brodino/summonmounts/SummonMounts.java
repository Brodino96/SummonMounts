package net.brodino.summonmounts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.brodino.summonmounts.commands.SpawnRandomHorseCommand;

public class SummonMounts implements ModInitializer {

    public static final String MOD_ID = "summonmounts";
    public static final Logger LOGGER = LoggerFactory.getLogger(SummonMounts.MOD_ID);
    public static net.brodino.summonmounts.Config CONFIG = net.brodino.summonmounts.Config.createAndLoad();
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

    }
}
