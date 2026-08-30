package dev.brodino.summonmounts.config;

import dev.brodino.summonmounts.config.data.flutes.OcarinaConfig;
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
    public OcarinaConfig ocarinas = new OcarinaConfig();
    public HashMap<String, Float> foodRepair = new HashMap<>(){{
        put("minecraft:hay_block", 1.0F);
    }};
    public List<MountEntry> mounts = List.of(
            new MountEntry("minecraft:donkey", OcarinaTypes.COPPER),
            new MountEntry("minecraft:mule", OcarinaTypes.IRON),
            new MountEntry("minecraft:horse", OcarinaTypes.GOLD)
    );
}
