package dev.brodino.summonmounts;

import dev.brodino.summonmounts.config.Config;
import dev.brodino.summonmounts.items.ItemManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummonMounts implements ModInitializer {

    public static final String MOD_ID = "summonmounts";
    public static final Logger LOGGER = LoggerFactory.getLogger(SummonMounts.MOD_ID);
    public static final Config CONFIG = new Config(MOD_ID, LOGGER);
    public static final boolean COMBATLOG_PRESENT = FabricLoader.getInstance().isModLoaded("combatlog");

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing SummonMounts");
        
        // Register event handlers
        EventHandlers.initialize();
        ItemManager.initialize();
    }
}
