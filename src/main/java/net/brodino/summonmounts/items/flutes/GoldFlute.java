package net.brodino.summonmounts.items.flutes;

import net.brodino.summonmounts.SummonMounts;
import net.minecraft.util.Rarity;

public class GoldFlute extends FluteItem {

    public GoldFlute() {
        super(FluteItem.SHARED_SETTINGS
                .maxDamage(SummonMounts.CONFIG.getData().flutes.gold.durability)
                .rarity(Rarity.RARE)
        );
    }
}
