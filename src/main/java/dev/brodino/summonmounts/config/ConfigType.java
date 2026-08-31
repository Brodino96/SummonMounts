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
            new MountEntry("minecraft:llama", OcarinaTypes.LESSER),

            new MountEntry("minecraft:horse", OcarinaTypes.MEDIUM),

            new MountEntry("mythicmounts:acencia", OcarinaTypes.GREATER),
            new MountEntry("mythicmounts:archelon", OcarinaTypes.GREATER),
            new MountEntry("mythicmounts:courierbird", OcarinaTypes.GREATER),
            new MountEntry("mythicmounts:direwolf", OcarinaTypes.GREATER),
            new MountEntry("mythicmounts:geckotoalizard", OcarinaTypes.GREATER),
            new MountEntry("mythicmounts:nightmare", OcarinaTypes.GREATER),
            new MountEntry("mythicmounts:nudibranch", OcarinaTypes.GREATER),
            new MountEntry("mythicmounts:ridinglizard", OcarinaTypes.GREATER),

            new MountEntry("mythicmounts:colelytra", OcarinaTypes.SUPERIOR),
            new MountEntry("mythicmounts:dragon", OcarinaTypes.SUPERIOR),
            new MountEntry("mythicmounts:firebird", OcarinaTypes.SUPERIOR),
            new MountEntry("mythicmounts:griffon", OcarinaTypes.SUPERIOR),
            new MountEntry("mythicmounts:moth", OcarinaTypes.SUPERIOR),
            new MountEntry("mythicmounts:netherbat", OcarinaTypes.SUPERIOR)
    );
}
