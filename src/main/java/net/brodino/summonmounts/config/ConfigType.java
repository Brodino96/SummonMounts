package net.brodino.summonmounts.config;

import net.brodino.summonmounts.config.data.FluteTypes;
import net.brodino.summonmounts.config.data.flutes.FlutesConfig;
import net.brodino.summonmounts.config.data.mounts.MountEntry;

import java.util.List;

public class ConfigType {

    public List<String> allowedDimensions = List.of("minecraft:overworld");
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
