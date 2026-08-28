package dev.brodino.summonmounts.items.flutes;

import dev.brodino.summonmounts.SummonMounts;
import net.minecraft.util.Rarity;

public class CopperFlute extends FluteItem {

    public CopperFlute() {
        super(FluteItem.BASE_SETTINGS
                .maxDamage(SummonMounts.CONFIG.getFlutes().copper.durability)
                .rarity(Rarity.COMMON)
        );
    }
}
