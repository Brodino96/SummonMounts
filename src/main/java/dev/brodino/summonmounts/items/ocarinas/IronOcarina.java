package dev.brodino.summonmounts.items.ocarinas;

import dev.brodino.summonmounts.items.OcarinaItem;
import dev.brodino.summonmounts.items.OcarinaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public class IronOcarina extends OcarinaItem {

    public IronOcarina() { super(OcarinaItem.BASE_SETTINGS, OcarinaTypes.IRON); }

    @Override
    public Rarity getRarity(ItemStack stack) { return Rarity.UNCOMMON; }
}
