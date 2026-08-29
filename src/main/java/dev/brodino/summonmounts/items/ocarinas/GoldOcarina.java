package dev.brodino.summonmounts.items.ocarinas;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.OcarinaItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class GoldOcarina extends OcarinaItem {

    public GoldOcarina() {
        super(OcarinaItem.BASE_SETTINGS
                .maxDamage(SummonMounts.CONFIG.getOcarinas().gold.durability)
        );
    }
}
