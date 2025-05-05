package net.brodino.summonmounts;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.UUID;

public class EventHandlers {

    /**
     * Registers all event handlers for the mod
     */
    public static void register() {

        // Register server start event to store server instance
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SummonMounts.SERVER = server;
        });

        // Register server tick event for mount timer handling
        ServerTickEvents.END_SERVER_TICK.register(EventHandlers::onServerTick);

        // Register player disconnect event
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            MountManager.playerDisconnected(handler.player);
        });

        // Register entity death event
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            EventHandlers.onMountDeath(entity);
        });
    }

    /**
     * Handles the death of a mount
     * @param entity The entity that died
     */
    private static void onMountDeath(Entity entity) {
        if (!(entity instanceof AbstractHorseEntity)) {
            return;
        }

        AbstractHorseEntity mount = (AbstractHorseEntity) entity;
        UUID ownerUUID = mount.getOwnerUuid();

        if (ownerUUID == null) {
            return;
        }

        ServerPlayerEntity owner = SummonMounts.SERVER.getPlayerManager().getPlayer(ownerUUID);
        if (owner == null) {
            return;
        }

        NBTHelper.saveMountData(mount, MountManager.playerItems.get(ownerUUID));
    }
    
    /**
     * Handles the server tick event to check mount timers
     * @param server The Minecraft server instance
     */
    private static void onServerTick(MinecraftServer server) {
        MountManager.tickMounts();
    }
    
    /**
     * Handles item use on entities (binding mounts)
     * @param player The player using the item
     * @param world The world
     * @param hand The hand used
     * @param target The entity being targeted
     * @return The action result
     */
    public static TypedActionResult<ItemStack> itemUsedOnAnEntity(PlayerEntity player, World world, Hand hand, Entity target, EntityHitResult hitResult) {

        ItemStack stack = player.getStackInHand(hand);

        if (world.isClient) {
            return TypedActionResult.pass(stack);
        }

        Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.summonItem()));

        if (stack.getItem() != summonItem) {
            return TypedActionResult.pass(stack);
        }

        if (!stack.hasNbt() || !stack.getNbt().contains("mount.genericData")) {
            if (MountManager.bindMountToItem(player, target, stack)) {
                return TypedActionResult.success(stack);
            }
        }
        
        return TypedActionResult.pass(stack);
    }
    
    /**
     * Handles item use (summoning or dismissing mounts)
     * @param player The player using the item
     * @param world The world
     * @param hand The hand used
     * @return The action result
     */
    public static TypedActionResult<ItemStack> onItemUse(PlayerEntity player, World world, Hand hand) {

        ItemStack stack = player.getStackInHand(hand);

        if (world.isClient) {
            return TypedActionResult.pass(stack);
        }

        Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.summonItem()));

        if (stack.getItem() != summonItem) {
            SummonMounts.LOGGER.info("Item is not the summon item");
            return TypedActionResult.pass(stack);
        }

        if (stack.hasNbt() && stack.getNbt().contains("mount.genericData")) {
            UUID playerUUID = player.getUuid();

            if (MountManager.hasActiveMount(playerUUID, stack)) {
                ItemStack out = MountManager.dismissMount(player);
                if (!out.equals(ItemStack.EMPTY)) {
                    player.setStackInHand(hand, out);
                    return TypedActionResult.success(stack);
                }
            } else {
                Entity mount = MountManager.summonMount(player, stack);
                if (mount != null) {
                    return TypedActionResult.success(stack);
                }
            }
        }
        
        return TypedActionResult.pass(stack);
    }
}