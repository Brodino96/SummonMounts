package dev.brodino.summonmounts.network;

import dev.brodino.summonmounts.SummonMounts;
import net.minecraft.util.Identifier;

public enum Packets {
    SUMMON(new Identifier(SummonMounts.MOD_ID, "summon_particles_packet")),
    RECALL(new Identifier(SummonMounts.MOD_ID, "recall_particles_packet")),
    FEED(new Identifier(SummonMounts.MOD_ID, "feed_particles_packet"));

    private final Identifier identifier;

    Packets(Identifier identifier) {
        this.identifier = identifier;
    }

    public Identifier getIdentifier() { return this.identifier; }
}
