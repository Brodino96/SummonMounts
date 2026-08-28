package dev.brodino.summonmounts.items.flutes;

import dev.brodino.summonmounts.SummonMounts;
import net.minecraft.util.Rarity;

public class DiamondFlute extends FluteItem {

    public DiamondFlute() {
        super(FluteItem.SHARED_SETTINGS
                .maxDamage(SummonMounts.CONFIG.getFlutes().diamond.durability)
                .rarity(Rarity.EPIC)
        );
    }
}
