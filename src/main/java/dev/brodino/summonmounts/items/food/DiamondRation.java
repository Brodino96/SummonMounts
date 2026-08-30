package dev.brodino.summonmounts.items.food;

import dev.brodino.summonmounts.items.OcarinaTypes;
import dev.brodino.summonmounts.items.RationItem;
import net.minecraft.util.Rarity;

public class DiamondRation extends RationItem {

    public DiamondRation() {
        super(RationItem.BASE_SETTINGS.rarity(Rarity.EPIC), OcarinaTypes.DIAMOND);
    }
}
