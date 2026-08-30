package dev.brodino.summonmounts.items.ocarinas;

import dev.brodino.summonmounts.items.OcarinaItem;
import dev.brodino.summonmounts.items.OcarinaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class CopperOcarina extends OcarinaItem {

    public CopperOcarina() { super(OcarinaItem.BASE_SETTINGS, OcarinaTypes.COPPER); }

    @Override
    public Rarity getRarity(ItemStack stack) { return Rarity.COMMON; }
}
