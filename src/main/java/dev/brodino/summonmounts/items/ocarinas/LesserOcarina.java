package dev.brodino.summonmounts.items.ocarinas;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.OcarinaItem;
import dev.brodino.summonmounts.items.OcarinaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class LesserOcarina extends OcarinaItem {

    public LesserOcarina() { super(
            OcarinaItem.BASE_SETTINGS
                    .maxDamage(SummonMounts.CONFIG.getOcarinaDurability(OcarinaTypes.LESSER)),
            OcarinaTypes.LESSER);
    }

    @Override
    public Rarity getRarity(ItemStack stack) { return Rarity.UNCOMMON; }
}
