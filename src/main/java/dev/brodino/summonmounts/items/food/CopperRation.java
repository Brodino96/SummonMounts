package dev.brodino.summonmounts.items.food;

import dev.brodino.summonmounts.items.OcarinaTypes;
import dev.brodino.summonmounts.items.RationItem;
import net.minecraft.util.Rarity;

public class CopperRation extends RationItem {

    public CopperRation() {
        super(RationItem.BASE_SETTINGS.rarity(Rarity.COMMON), OcarinaTypes.COPPER);
    }
}
