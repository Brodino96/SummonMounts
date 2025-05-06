package net.brodino.summonmounts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MountManager {
    private static final Map<UUID, UUID> playerMounts = new HashMap<>();
    private static final Map<UUID, ItemStack> playerItems = new HashMap<>();
    private static final Map<UUID, Integer> mountTimers = new HashMap<>();
    private static final int DESPAWN_TIMER = SummonMounts.CONFIG.despawnTime() * 20;


    /**
     * Method to associate a clean mount to an item
     * @param player The player using the item
     * @param entity The target entity
     * @param stack The ItemStack used
     * @return true if mount get linked
     */
    public static boolean bindMountToItem(PlayerEntity player, Entity entity, ItemStack stack) {

        String playerName = player.getDisplayName().getString();
        SummonMounts.LOGGER.info("Player: [{}] is trying to bound a mount", playerName);

        if (!(entity instanceof AbstractHorseEntity)) {
            SummonMounts.LOGGER.info("Player: [{}] used the item on a mount that wasn't valid", playerName);
            return false;
        }

        UUID mountUuid = entity.getUuid();
        if (playerMounts.containsValue(mountUuid)) {
            SummonMounts.LOGGER.info("Player: [{}] used the item on a mount that wasn't his", playerName);
            player.sendMessage(Text.literal("L'essenza di questa creatura è già legata a qualcosa"), true);
            return false;
        }

        AbstractHorseEntity mount = (AbstractHorseEntity) entity;

        if (!mount.isTame() || !player.getUuid().equals(mount.getOwnerUuid())) {
            SummonMounts.LOGGER.info("Player: [{}] used the item on a mount that wasn't his", playerName);
            player.sendMessage(Text.literal("Puoi legare l'essenza solo di una creatura a te familiare"), true);
            return false;
        }

        String mountId = Registry.ENTITY_TYPE.getId(mount.getType()).toString();
        boolean isAllowed = false;
        for (String allowedType : SummonMounts.CONFIG.allowedSummons()) {
            if (allowedType.equals(mountId)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            SummonMounts.LOGGER.info("Player: [{}] used the item on a mount that wasn't valid", playerName);
            player.sendMessage(Text.literal("Questo tipo di creatura non può essere legata"), true);
            return false;
        }

        NBTHelper.saveMountData(mount, stack);
        mount.discard();

        SummonMounts.LOGGER.info("Player: [{}] successfully bound a mount", playerName);
        player.sendMessage(Text.literal("Creatura legata con successo"), true);

        return true;
    }

    public static Entity summonMount(PlayerEntity player, ItemStack stack) {

        String playerName = player.getDisplayName().getString();

        SummonMounts.LOGGER.info("Player: [{}] is trying to summon a mount", playerName);

        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("mount.type")) {
            SummonMounts.LOGGER.info("Player: [{}] tried to use an item that didn't have a mount bound to it", playerName);
            player.sendMessage(Text.literal("Questo oggetto non ha alcuna creatura legata ad esso"), true);
            return null;
        }

        UUID playerUuid = player.getUuid();

        String mountTypeId = nbt.getString("mount.type");
        World world = SummonMounts.SERVER.getOverworld();
        EntityType<?> entityType = Registry.ENTITY_TYPE.get(new Identifier(mountTypeId));
        if (entityType == null) {
            SummonMounts.LOGGER.info("Player: [{}] tried to use an item that had un unspecified mount bound to it", playerName);
            player.sendMessage(Text.literal("Invalid mount type!"), true);
            return null;
        }

        Entity mount = entityType.create(world);
        if (mount == null) {
            SummonMounts.LOGGER.info("Player: [{}] failed to create the mount", playerName);
            player.sendMessage(Text.literal("Failed to create mount"), true);
            return null;
        }

        mount = NBTHelper.loadMountData((AbstractHorseEntity) mount, nbt);

        mount.setPosition(player.getX(), player.getY(), player.getZ());
        world.spawnEntity(mount);

        playerMounts.put(playerUuid, mount.getUuid());
        playerItems.put(playerUuid, stack.copy());

        SummonMounts.LOGGER.info("Player: [{}] successfully summoned his mount", playerName);
        player.sendMessage(Text.literal("Cavalcatura evocata"), true);

        return mount;
    }

    public static ItemStack dismissMount(PlayerEntity player) {
        String playerName = player.getDisplayName().getString();
        UUID playerUUID = player.getUuid();

        SummonMounts.LOGGER.info("Player: [{}] is trying to dismiss a mount", playerName);

        if (!playerMounts.containsKey(playerUUID)) {
            SummonMounts.LOGGER.info("Player: [{}] had no mounts summoned", playerName);
            return ItemStack.EMPTY;
        }

        UUID mountUUID = playerMounts.get(playerUUID);

        Entity mount = SummonMounts.SERVER.getOverworld().getEntity(mountUUID);
        if (mount == null || !mount.isAlive()) {
            SummonMounts.LOGGER.info("Player: [{}] didn't have any alive mounts", playerName);
            return ItemStack.EMPTY;
        }

        ItemStack output = NBTHelper.saveMountData(mount, playerItems.get(playerUUID));

        mount.discard();
        SummonMounts.LOGGER.info("Player: [{}] mount got removed from the world", playerName);
        player.sendMessage(Text.literal("La tua cavalcatura è stata richiamata"), true);


        playerMounts.remove(playerUUID);
        mountTimers.remove(mountUUID);
        playerItems.remove(playerUUID);

        return output;
    }

    public static void tickMounts() {
        Map<UUID, UUID> mountsCopy = new HashMap<>(playerMounts);

        for (Map.Entry<UUID, UUID> entry : mountsCopy.entrySet()) {
            UUID playerUUID = entry.getKey();
            UUID mountUUID = entry.getValue();

            Entity mount = SummonMounts.SERVER.getOverworld().getEntity(mountUUID);
            if (mount == null || !mount.isAlive()) {
                playerMounts.remove(playerUUID);
                mountTimers.remove(mountUUID);
                continue;
            }


            if (mount.hasPassengers()) {
                mountTimers.remove(mountUUID);
            } else {
                int timer = mountTimers.getOrDefault(mountUUID, DESPAWN_TIMER);
                timer--;

                if (timer <= 0) {

                    ServerPlayerEntity owner = SummonMounts.SERVER.getPlayerManager().getPlayer(playerUUID);

                    if (owner != null) {
                        SummonMounts.LOGGER.info("Automatically dismissing mount for player: {}", owner.getName().toString());
                        ItemStack updatedItem = MountManager.dismissMount(owner);
                        
                        // Find the summon item in player's inventory and replace it with the updated one
                        if (!updatedItem.isEmpty()) {
                            Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.summonItem()));
                            for (int i = 0; i < owner.getInventory().size(); i++) {
                                ItemStack stack = owner.getInventory().getStack(i);
                                if (stack.getItem() == summonItem && stack.getNbt().getUuid("mount.uuid").equals(mount.getUuid())) {
                                    SummonMounts.LOGGER.info("dnwoadoiwando naid aowidan wndpan wdona");
                                    owner.getInventory().setStack(i, updatedItem);
                                    break;
                                }
                            }
                        }
                    }

                } else {
                    mountTimers.put(mountUUID, timer);
                }
            }

        }
    }

    public static void playerDisconnected(ServerPlayerEntity player) {

        String playerName = player.getDisplayName().getString();

        SummonMounts.LOGGER.info("Player: [{}] left the server or the overworld", playerName);

        UUID playerUUID = player.getUuid();
        if (!playerMounts.containsKey(playerUUID)) {
            SummonMounts.LOGGER.info("Player: [{}] didn't have any mount", playerName);
            return;
        }

        UUID mountUUID = playerMounts.get(playerUUID);
        Entity mount = SummonMounts.SERVER.getOverworld().getEntity(mountUUID);

        if (mount != null && mount.isAlive()) {
            ItemStack updatedItem = MountManager.dismissMount(player);
            
            // Save the updated item to player's inventory for when they reconnect
            if (!updatedItem.isEmpty()) {
                Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.summonItem()));
                for (int i = 0; i < player.getInventory().size(); i++) {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (stack.getItem() == summonItem && stack.getNbt().getUuid("mount.uuid").equals(mount.getUuid())) {
                        SummonMounts.LOGGER.info("dnwoadoiwando naid aowidan wndpan wdona");
                        player.getInventory().setStack(i, updatedItem);
                        break;
                    }
                }
            }
        }
    }

    public static boolean hasActiveMount(UUID playerUUID, ItemStack stack) {
        if (!playerMounts.containsKey(playerUUID)) {
            return false;
        }

        UUID mountUUID = playerMounts.get(playerUUID);

        if (SummonMounts.SERVER == null) return false;

        Entity mount = SummonMounts.SERVER.getOverworld().getEntity(mountUUID);
        if (mount == null || !mount.isAlive()) {
            playerMounts.remove(playerUUID);
            playerItems.remove(playerUUID);
            mountTimers.remove(mountUUID);
            return false;
        }

        return true;
    }

    /**
     * Handles the death of a mount
     * @param entity The entity that died
     */
    public static void onMountDeath(Entity entity) {
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
        SummonMounts.LOGGER.info("Player: [{}] mount died", owner.getDisplayName());

        ItemStack updatedItem = NBTHelper.saveMountData(mount, MountManager.playerItems.get(ownerUUID));
        
        // Find the summon item in player's inventory and replace it with the updated one
        if (!updatedItem.isEmpty()) {
            Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.summonItem()));
            for (int i = 0; i < owner.getInventory().size(); i++) {
                ItemStack stack = owner.getInventory().getStack(i);
                if (stack.getItem() == summonItem && stack.getNbt().getUuid("mount.uuid").equals(mount.getUuid())) {
                    SummonMounts.LOGGER.info("dnwoadoiwando naid aowidan wndpan wdona");
                    owner.getInventory().setStack(i, updatedItem);
                    break;
                }
            }
        }
        
        // Clean up the maps
        playerMounts.remove(ownerUUID);
        mountTimers.remove(entity.getUuid());
        playerItems.remove(ownerUUID);
    }

    /**
     * Handles item use on entities (binding mounts)
     *
     * @param player The player using the item
     * @param world  The world
     * @param hand   The hand used
     * @param target The entity being targeted
     */
    public static void itemUsedOnAnEntity(PlayerEntity player, World world, Hand hand, Entity target, EntityHitResult hitResult) {

        ItemStack stack = player.getStackInHand(hand);

        Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.summonItem()));

        if (stack.getItem() != summonItem) {
            return;
        }

        if (!stack.hasNbt() || !stack.getNbt().contains("mount.genericData")) {
            if (MountManager.bindMountToItem(player, target, stack)) {
            }
        }

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
        Item summonItem = Registry.ITEM.get(new Identifier(SummonMounts.CONFIG.summonItem()));

        if (stack.getItem() != summonItem) {
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
























