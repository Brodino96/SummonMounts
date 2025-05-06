package net.brodino.summonmounts;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;

public class EventHandlers {

    /**
     * Registers all event handlers for the mod
     */
    public static void register() {

        // Register server start event to store server instance
        SummonMounts.LOGGER.info("Storing the server inside the mod");
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SummonMounts.SERVER = server;
        });

        // Register server tick event for mount timer handling
        SummonMounts.LOGGER.info("Registering tick event");
        ServerTickEvents.END_SERVER_TICK.register(EventHandlers::onServerTick);

        // Register player disconnect event
        SummonMounts.LOGGER.info("Registering disconnect event");
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            MountManager.playerDisconnected(handler.player);
        });

        // Register entity death event
        SummonMounts.LOGGER.info("Registering death event");
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            MountManager.onMountDeath(entity);
        });

        // Register item use on entity callbacks
        SummonMounts.LOGGER.info("Registering itemUseOnEntity event");
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            MountManager.itemUsedOnAnEntity(player, world, hand, entity, hitResult);
            return ActionResult.PASS;
        });

        // Register item use callbacks
        SummonMounts.LOGGER.info("Registering item use event");
        UseItemCallback.EVENT.register(MountManager::onItemUse);
    }
    
    /**
     * Handles the server tick event to check mount timers
     * @param server The Minecraft server instance
     */
    private static void onServerTick(MinecraftServer server) {
        MountManager.tickMounts();
    }

}