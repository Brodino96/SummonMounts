package dev.brodino.summonmounts.items.ocarinas;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.OcarinaItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class IronOcarina extends OcarinaItem {

    public IronOcarina() {
        super(OcarinaItem.BASE_SETTINGS
                .maxDamage(SummonMounts.CONFIG.getOcarinas().iron.durability)
        );
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }
}
