package net.brodino.summonmounts;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummonMounts implements ModInitializer {

    public static final String MOD_ID = "summonmounts";
    public static final Logger LOGGER = LoggerFactory.getLogger(SummonMounts.MOD_ID);
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing SummonMounts");


    }
}
