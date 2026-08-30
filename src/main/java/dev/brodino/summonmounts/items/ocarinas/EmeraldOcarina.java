package dev.brodino.summonmounts.items.ocarinas;

import dev.brodino.summonmounts.items.OcarinaItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class EmeraldOcarina extends OcarinaItem {

    public EmeraldOcarina() { super(OcarinaItem.BASE_SETTINGS); }

    @Override
    public Rarity getRarity(ItemStack stack) { return Rarity.EPIC; }
}
