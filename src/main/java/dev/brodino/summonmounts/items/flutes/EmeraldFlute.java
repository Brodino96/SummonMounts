package dev.brodino.summonmounts.items.flutes;

import dev.brodino.summonmounts.SummonMounts;
import net.minecraft.util.Rarity;

public class EmeraldFlute extends FluteItem {

    public EmeraldFlute() {
        super(FluteItem.SHARED_SETTINGS
                .maxDamage(SummonMounts.CONFIG.getData().flutes.emerald.durability)
                .rarity(Rarity.EPIC)
        );
    }
}
