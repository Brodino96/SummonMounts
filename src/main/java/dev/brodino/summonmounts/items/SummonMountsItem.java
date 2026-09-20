package dev.brodino.summonmounts.items;

import net.minecraft.item.Item;

public class SummonMountsItem extends Item {

    private final OcarinaTypes type;

    public SummonMountsItem(Settings settings, OcarinaTypes type) {
        super(settings);
        this.type = type;
    }

    public OcarinaTypes getType() { return this.type; }
}
