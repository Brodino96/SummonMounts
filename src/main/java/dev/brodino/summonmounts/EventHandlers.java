package dev.brodino.summonmounts;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class EventHandlers {

    public static void initialize() {

        // Register server tick event for mount timer handling
        SummonMounts.LOGGER.info("Registering tick event");
        ServerTickEvents.END_SERVER_TICK.register(MountManager::tick);

        // Register player disconnect event
        SummonMounts.LOGGER.info("Registering disconnect event");
        ServerPlayConnectionEvents.DISCONNECT.register(MountManager::onPlayerDisconnect);

        // Register entity death event - use ALLOW_DEATH to intercept before items drop
        SummonMounts.LOGGER.info("Registering death event");
        ServerLivingEntityEvents.ALLOW_DEATH.register(MountManager::onMountDeath);

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(MountManager::onDimensionChange);
    }

}