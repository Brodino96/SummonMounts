package dev.brodino.summonmounts.items.food;

import dev.brodino.summonmounts.items.OcarinaTypes;
import dev.brodino.summonmounts.items.RationItem;
import net.minecraft.util.Rarity;

public class IronRation extends RationItem {

    public IronRation() {
        super(RationItem.BASE_SETTINGS.rarity(Rarity.UNCOMMON), OcarinaTypes.IRON);
    }
}
