package dev.brodino.summonmounts.config;

import dev.brodino.summonmounts.config.data.flutes.FlutesConfig;
import dev.brodino.summonmounts.config.data.mounts.MountEntry;
import dev.brodino.summonmounts.items.FluteTypes;

import java.util.List;

public class ConfigType {

    public List<String> allowedDimensions = List.of("minecraft:overworld");
    public int flutesCooldownSeconds = 1;
    public int mountAliveSeconds = 5 * 60;
    public int mountIdleSeconds = 30;
    public int mountAirborneSeconds = 2 * 60;
    public FlutesConfig flutes = new FlutesConfig();
    public List<MountEntry> mounts = List.of(
            new MountEntry("minecraft:donkey", FluteTypes.COPPER),
            new MountEntry("minecraft:mule", FluteTypes.IRON),
            new MountEntry("minecraft:horse", FluteTypes.GOLD)
    );
}
