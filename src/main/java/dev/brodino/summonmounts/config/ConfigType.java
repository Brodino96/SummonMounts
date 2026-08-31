package dev.brodino.summonmounts.config;

import dev.brodino.summonmounts.config.data.mounts.MountEntry;
import dev.brodino.summonmounts.items.OcarinaTypes;

import java.util.HashMap;
import java.util.List;

public class ConfigType {

    public List<String> allowedDimensions = List.of("minecraft:overworld");
    public int ocarinasCooldownSeconds = 1;
    public int mountAliveSeconds = 5 * 60;
    public int mountIdleSeconds = 30;
    public int mountAirborneSeconds = 2 * 60;
    public HashMap<OcarinaTypes, Float> foodRepair = new HashMap<>(){{
        put(OcarinaTypes.INFERIOR, 1.0F);
        put(OcarinaTypes.LESSER, 1.0F);
        put(OcarinaTypes.MEDIUM, 1.0F);
        put(OcarinaTypes.GREATER, 1.0F);
        put(OcarinaTypes.SUPERIOR, 1.0F);
    }};
    public List<MountEntry> mounts = List.of(
            new MountEntry("minecraft:donkey", OcarinaTypes.INFERIOR),
            new MountEntry("minecraft:mule", OcarinaTypes.LESSER),
            new MountEntry("minecraft:horse", OcarinaTypes.MEDIUM)
    );
}
