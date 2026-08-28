package dev.brodino.summonmounts.items.flutes;

import dev.brodino.summonmounts.SummonMounts;
import net.minecraft.util.Rarity;

public class GoldFlute extends FluteItem {

    public GoldFlute() {
        super(FluteItem.BASE_SETTINGS
                .maxDamage(SummonMounts.CONFIG.getFlutes().gold.durability)
                .rarity(Rarity.RARE)
        );
    }
}
