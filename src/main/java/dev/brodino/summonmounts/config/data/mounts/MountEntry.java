package dev.brodino.summonmounts.config.data.mounts;

import dev.brodino.summonmounts.items.OcarinaTypes;

public class MountEntry {

    public String id;
    public OcarinaTypes ocarina;

    public MountEntry(String id, OcarinaTypes ocarina) {
        this.id = id;
        this.ocarina = ocarina;
    }
}
