package dev.brodino.summonmounts.items.ocarinas;

import dev.brodino.summonmounts.items.OcarinaItem;
import dev.brodino.summonmounts.items.OcarinaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class DiamondOcarina extends OcarinaItem {

    public DiamondOcarina() { super(OcarinaItem.BASE_SETTINGS, OcarinaTypes.DIAMOND); }

    @Override
    public Rarity getRarity(ItemStack stack) { return Rarity.EPIC; }
}
