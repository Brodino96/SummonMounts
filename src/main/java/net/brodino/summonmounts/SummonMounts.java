package net.brodino.summonmounts;

import com.mojang.brigadier.CommandDispatcher;
import net.brodino.summonmounts.commands.ReloadConfig;
import net.brodino.summonmounts.commands.SpawnHorse;
import net.fabricmc.api.ModInitializer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummonMounts implements ModInitializer {

    public static final String MOD_ID = "summonmounts";
    public static final Logger LOGGER = LoggerFactory.getLogger(SummonMounts.MOD_ID);
    public static Config CONFIG = new Config();
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing SummonMounts");
        
        // Register event handlers
        EventHandlers.initialize();
    }

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess _r, CommandManager.RegistrationEnvironment _e) {
        dispatcher.register(CommandManager.literal(SummonMounts.MOD_ID)
            .requires(src -> src.hasPermissionLevel(2))
            .then(CommandManager.literal("spawnHorse")
                .executes(SpawnHorse::execute)
            )
        );
    }
}
