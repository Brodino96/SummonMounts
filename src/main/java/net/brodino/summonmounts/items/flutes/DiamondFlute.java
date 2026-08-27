package net.brodino.summonmounts.items.flutes;

import net.brodino.summonmounts.SummonMounts;
import net.minecraft.util.Rarity;

public class DiamondFlute extends FluteItem {

    public DiamondFlute() {
        super(FluteItem.SHARED_SETTINGS
                .maxDamage(SummonMounts.CONFIG.getData().flutes.diamond.durability)
                .rarity(Rarity.EPIC)
        );
    }
}
