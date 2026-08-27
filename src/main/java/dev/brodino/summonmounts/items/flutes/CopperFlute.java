package dev.brodino.summonmounts.items.flutes;

import dev.brodino.summonmounts.SummonMounts;
import net.minecraft.util.Rarity;

public class CopperFlute extends FluteItem {

    public CopperFlute() {
        super(FluteItem.SHARED_SETTINGS
                .maxDamage(SummonMounts.CONFIG.getData().flutes.copper.durability)
                .rarity(Rarity.COMMON)
        );
    }
}
