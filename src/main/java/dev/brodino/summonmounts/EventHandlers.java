package dev.brodino.summonmounts;

import dev.brodino.summonmounts.commands.CommandHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class EventHandlers {

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(MountManager::tick);

        ServerPlayConnectionEvents.DISCONNECT.register(MountManager::onPlayerDisconnect);

        ServerLivingEntityEvents.ALLOW_DEATH.register(MountManager::onMountDeath);

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(MountManager::onDimensionChange);

        CommandRegistrationCallback.EVENT.register(CommandHandler::initialize);

        ServerLifecycleEvents.SERVER_STOPPING.register(MountManager::recallAllMounts);

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((ms, r, s) -> {
            SummonMounts.CONFIG.reload();
        });
    }

}