package dev.brodino.summonmounts.network;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ForceLandTracker {

    private static final Set<UUID> FORCE_LANDING = new HashSet<>();

    public static boolean isForceLanding(UUID uuid) { return FORCE_LANDING.contains(uuid); }

    public static void setForceLanding(UUID uuid, boolean forceLanding) {
        if (forceLanding) {
            FORCE_LANDING.add(uuid);
        } else {
            FORCE_LANDING.remove(uuid);
        }
    }

    public static void clear() { FORCE_LANDING.clear(); }
}
