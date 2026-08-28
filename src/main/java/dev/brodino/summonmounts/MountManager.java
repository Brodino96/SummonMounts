package dev.brodino.summonmounts;

import dev.brodino.summonmounts.mount.Mount;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

@Environment(EnvType.SERVER)
public class MountManager {

    private static final HashMap<UUID, Mount> MOUNTS = new HashMap<>();

    public static boolean hasActiveMount(PlayerEntity player) { return MOUNTS.containsKey(player.getUuid()); }
    public static Mount getActiveMount(PlayerEntity player) { return MOUNTS.get(player.getUuid()); }
    private static Optional<Mount> getMountFromEntity(LivingEntity entity) {
        return MOUNTS.values().stream().filter(mount -> mount.equals(entity)).findFirst();
    }

    public static void summon(PlayerEntity player, Mount mount) {
        if (hasActiveMount(player)) return;
        mount.summon();
        MOUNTS.put(player.getUuid(), mount);
    }

    public static void recall(PlayerEntity player) {
        Mount mount = getActiveMount(player);
        if (mount == null) return;
        mount.recall();
        MOUNTS.remove(player.getUuid());
    }

    // Events

    public static boolean onMountDeath(LivingEntity entity, DamageSource source, float amount) {
        Optional<Mount> mountOptional = getMountFromEntity(entity);
        if (mountOptional.isEmpty()) {
            return true;
        }

        Mount mount = mountOptional.get();
        recall(mount.getSummoner());
        return false;
    }

    public static void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        SummonMounts.LOGGER.info("Recalling {}'s mount because trying to leave the server", handler.player.getName().getString());
        recall(handler.player);
    }

    public static void onDimensionChange(ServerPlayerEntity player, ServerWorld from, ServerWorld to) {
        if (from.equals(to)) return;
        SummonMounts.LOGGER.info("Recalling {}'s mount because changed dimension", player.getName().getString());
        recall(player);
    }

    public static void tick() {
        Iterator<Map.Entry<UUID, Mount>> iterator = MOUNTS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Mount> entry = iterator.next();

            // Tick every mount
            iterator.remove();
        }
    }
}
