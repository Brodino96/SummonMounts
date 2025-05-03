package net.brodino.summonmounts;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MountManager {
    private static final Map<UUID, UUID> playerMounts = new HashMap<>();
    private static final Map<UUID, Integer> mountTimers = new HashMap<>();
    public static final Map<UUID, ItemStack> playerItems = new HashMap<>();
    private static final int DESPAWN_TIMER = 20 * 20;

    public static boolean bindMountToItem(PlayerEntity player, Entity entity, ItemStack stack) {
        if (!(entity instanceof AbstractHorseEntity)) {
            return false;
        }

        UUID mountUuid = entity.getUuid();
        if (playerMounts.containsValue(mountUuid)) {
            player.sendMessage(Text.literal("L'essenza di questa creatura è già legata a qualcosa"), true);
            return false;
        }

        AbstractHorseEntity mount = (AbstractHorseEntity) entity;

        if (!mount.isTame() || !player.getUuid().equals(mount.getOwnerUuid())) {
            player.sendMessage(Text.literal("Puoi legare l'essenza solo di una creatura a te familiare"), true);
            return false;
        }

        String mountId = Registry.ENTITY_TYPE.getId(mount.getType()).toString();
        boolean isAllowed = false;
        for (String allowedType : Summonmounts.CONFIG.allowedSummons()) {
            if (allowedType.equals(mountId)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            player.sendMessage(Text.literal("Questo tipo di creatura non può essere legata"), true);
            return false;
        }

        stack = NBTHelper.saveMountData(player, mount, stack);

        mount.discard();

        stack.addEnchantment(Enchantments.LOYALTY, 1);

        player.sendMessage(Text.literal("Creatura legata con successo"), true);

        return true;
    }

    public static Entity summonMount(PlayerEntity player, ItemStack stack) {

        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("mount.type")) {
            player.sendMessage(Text.literal("Questo oggetto non ha alcuna creatura legata ad esso"), true);
            return null;
        }

        UUID playerUuid = player.getUuid();
        if (playerMounts.containsKey(playerUuid)) {
            UUID existingMountUUID = playerMounts.get(playerUuid);
            Entity existingMount = player.getServer().getOverworld().getEntity(existingMountUUID);

            if (existingMount != null && existingMount.isAlive()) {
                existingMount.discard();
                player.sendMessage(Text.literal("La tua cavalcatura precedente è stata richiamata"), true);
            }

            playerMounts.remove(playerUuid);
        }

        String mountTypeId = nbt.getString("mount.type");
        World world = player.getWorld();
        EntityType<?> entityType = Registry.ENTITY_TYPE.get(new Identifier(mountTypeId));
        if (entityType == null) {
            player.sendMessage(Text.literal("Invalid mount type!"), true);
            return null;
        }

        Entity mount = entityType.create(world);
        if (mount == null) {
            player.sendMessage(Text.literal("Failed to create mount"), true);
            return null;
        }

        NBTHelper.loadMountData(mount, nbt);

        mount.setPosition(player.getX(), player.getY(), player.getZ());
        world.spawnEntity(mount);

        playerMounts.put(playerUuid, mount.getUuid());
        playerItems.put(playerUuid, stack.copy());

        player.sendMessage(Text.literal("Cavalcatura evocata"), true);

        return mount;
    }

    public static boolean dismissMount(PlayerEntity player) {
        UUID playerUUID = player.getUuid();

        if (!playerMounts.containsKey(playerUUID)) {
            return false;
        }

        UUID mountUUID = playerMounts.get(playerUUID);

        Entity mount = player.getServer().getOverworld().getEntity(mountUUID);
        if (mount != null && mount.isAlive()) {
            NBTHelper.saveMountData(player, mount, playerItems.get(playerUUID));

            mount.discard();
            player.sendMessage(Text.literal("La tua cavalcatura è stata richiamata"), true);
        }

        playerMounts.remove(playerUUID);
        mountTimers.remove(mountUUID);
        playerItems.remove(playerUUID);

        return true;
    }

    public static void tickMounts() {
        Map<UUID, UUID> mountsCopy = new HashMap<>(playerMounts);

        for (Map.Entry<UUID, UUID> entry : mountsCopy.entrySet()) {
            UUID playerUUID = entry.getKey();
            UUID mountUUID = entry.getValue();

            /*
            ServerPlayerEntity anyPlayer = null;
            for (ServerPlayerEntity player: Summonmounts.SERVER.getPlayerManager().getPlayerList()) {
                anyPlayer = player;
                break;
            }

            if (anyPlayer == null) continue;

            Entity mount = anyPlayer.getServer().getOverworld().getEntity(mountUUID);
             */

            Entity mount = Summonmounts.SERVER.getOverworld().getEntity(mountUUID);
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
                    ServerPlayerEntity owner = Summonmounts.SERVER.getPlayerManager().getPlayer(playerUUID);
                    // ServerPlayerEntity owner = anyPlayer.getServer().getPlayerManager().getPlayer(playerUUID);
                    if (owner != null) {
                        NBTHelper.saveMountData(owner, mount, playerItems.get(playerUUID));
                        owner.sendMessage(Text.literal("La tua cavalcatura è stata richiamata"), true);
                    }

                    mount.discard();
                    playerMounts.remove(playerUUID);
                    playerItems.remove(playerUUID);
                    mountTimers.remove(mountUUID);
                } else {
                    mountTimers.put(mountUUID, timer);
                }
            }

        }
    }

    public static void playerDisconnected(ServerPlayerEntity player) {
        UUID playerUUID = player.getUuid();
        if (!playerMounts.containsKey(playerUUID)) {
            return;
        }

        UUID mountUUID = playerMounts.get(playerUUID);
        Entity mount = player.getServer().getOverworld().getEntity(mountUUID);

        if (mount != null && mount.isAlive()) {
            NBTHelper.saveMountData(player, mount, playerItems.get(playerUUID));
            mount.discard();
        }

        playerMounts.remove(playerUUID);
        playerItems.remove(playerUUID);
        mountTimers.remove(mountUUID);
    }

    public static boolean hasActiveMount(UUID playerUUID, ItemStack stack) {
        if (!playerMounts.containsKey(playerUUID)) {
            return false;
        }

        UUID mountUUID = playerMounts.get(playerUUID);

        if (Summonmounts.SERVER == null) return false;

        Entity mount = Summonmounts.SERVER.getOverworld().getEntity(mountUUID);
        if (mount == null || !mount.isAlive()) {
            playerMounts.remove(playerUUID);
            playerItems.remove(playerUUID);
            mountTimers.remove(mountUUID);
            return false;
        }

        return true;
    }
}
























