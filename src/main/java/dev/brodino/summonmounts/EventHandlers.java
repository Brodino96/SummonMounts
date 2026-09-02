package dev.brodino.summonmounts;

import dev.brodino.summonmounts.commands.CommandHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class EventHandlers {

    public static void initialize() {
        SummonMounts.LOGGER.info("Registering tick event");
        ServerTickEvents.END_SERVER_TICK.register(MountManager::tick);

        SummonMounts.LOGGER.info("Registering disconnect event");
        ServerPlayConnectionEvents.DISCONNECT.register(MountManager::onPlayerDisconnect);

        SummonMounts.LOGGER.info("Registering death event");
        ServerLivingEntityEvents.ALLOW_DEATH.register(MountManager::onMountDeath);

        SummonMounts.LOGGER.info("Registering player change event");
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(MountManager::onDimensionChange);

        SummonMounts.LOGGER.info("Registering command");
        CommandRegistrationCallback.EVENT.register(CommandHandler::initialize);
    }

}