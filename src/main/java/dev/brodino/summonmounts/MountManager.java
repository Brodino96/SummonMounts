package dev.brodino.summonmounts;

import dev.brodino.summonmounts.mount.Mount;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MountManager {

    private static final HashMap<UUID, Mount> MOUNTS = new HashMap<>();

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

    public static boolean hasActiveMount(PlayerEntity player) {
        return MOUNTS.containsKey(player.getUuid());
    }

    public static Mount getActiveMount(PlayerEntity player) {
        return MOUNTS.get(player.getUuid());
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
