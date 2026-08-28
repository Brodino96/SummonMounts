package dev.brodino.summonmounts.items.flutes;

import dev.brodino.summonmounts.SummonMounts;
import net.minecraft.util.Rarity;

public class IronFlute extends FluteItem {

    public IronFlute() {
        super(FluteItem.BASE_SETTINGS
                .maxDamage(SummonMounts.CONFIG.getFlutes().iron.durability)
                .rarity(Rarity.UNCOMMON)
        );
    }
}
