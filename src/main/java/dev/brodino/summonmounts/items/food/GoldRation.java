package dev.brodino.summonmounts.items.food;

import dev.brodino.summonmounts.items.OcarinaTypes;
import dev.brodino.summonmounts.items.RationItem;
import net.minecraft.util.Rarity;

public class GoldRation extends RationItem {

    public GoldRation() {
        super(RationItem.BASE_SETTINGS.rarity(Rarity.RARE), OcarinaTypes.GOLD);
    }
}
