package net.brodino.summonmounts.config.data.mounts;

import net.brodino.summonmounts.items.FluteTypes;

public class MountEntry {

    public String id;
    public FluteTypes flute;

    public MountEntry(String id, FluteTypes type) {
        this.id = id;
        this.flute = type;
    }
}
