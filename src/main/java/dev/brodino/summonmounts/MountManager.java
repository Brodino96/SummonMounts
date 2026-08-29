package dev.brodino.summonmounts;

import dev.brodino.summonmounts.mount.Mount;
import dev.brodino.summonmounts.mount.RecallReason;
import fabric.me.toastymop.combatlog.util.IEntityDataSaver;
import fabric.me.toastymop.combatlog.util.TagData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class MountManager {

    private static final HashMap<UUID, Mount> MOUNTS = new HashMap<>();

    public static boolean hasActiveMount(PlayerEntity player) { return MOUNTS.containsKey(player.getUuid()); }
    public static Mount getActiveMount(PlayerEntity player) { return MOUNTS.get(player.getUuid()); }
    private static Optional<Mount> getMountFromEntity(LivingEntity entity) {
        return MOUNTS.values().stream().filter(mount -> mount.getUuid().equals(entity.getUuid())).findFirst();
    }

    public static boolean summon(PlayerEntity player, Mount mount) {
        if (hasActiveMount(player)) return false;
        mount.summon();
        MOUNTS.put(player.getUuid(), mount);
        return true;
    }

    public static void recall(PlayerEntity player, RecallReason reason) {
        Mount mount = getActiveMount(player);
        if (mount == null) return;
        mount.recall(reason);
        MOUNTS.remove(player.getUuid());
    }

    // Events

    public static boolean onMountDeath(LivingEntity entity, DamageSource source, float amount) {
        Optional<Mount> mountOptional = getMountFromEntity(entity);
        if (mountOptional.isEmpty()) {
            return true;
        }

        Mount mount = mountOptional.get();
        recall(mount.getSummoner(), RecallReason.DEATH);
        return false;
    }

    public static void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        recall(handler.player, RecallReason.DISCONNECT);
    }

    public static void onDimensionChange(ServerPlayerEntity player, ServerWorld from, ServerWorld to) {
        if (from.equals(to)) return;
        recall(player, RecallReason.DIMENSION);
    }

    public static void tick(MinecraftServer server) {
        Iterator<Map.Entry<UUID, Mount>> iterator = MOUNTS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Mount> entry = iterator.next();

            Mount mount = entry.getValue();
            RecallReason reason = mount.tick();
            if (reason != RecallReason.NONE) {
                mount.recall(reason);
                iterator.remove();
            }
        }
    }
}
