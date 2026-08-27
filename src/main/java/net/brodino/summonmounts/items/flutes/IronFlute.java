package net.brodino.summonmounts.items.flutes;

import net.brodino.summonmounts.SummonMounts;
import net.minecraft.util.Rarity;

public class IronFlute extends FluteItem {

    public IronFlute() {
        super(FluteItem.SHARED_SETTINGS
                .maxDamage(SummonMounts.CONFIG.getData().flutes.iron.durability)
                .rarity(Rarity.UNCOMMON)
        );
    }
}
