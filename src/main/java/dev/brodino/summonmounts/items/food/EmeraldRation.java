package dev.brodino.summonmounts.items.food;

import dev.brodino.summonmounts.items.OcarinaTypes;
import dev.brodino.summonmounts.items.RationItem;
import net.minecraft.util.Rarity;

public class EmeraldRation extends RationItem {

    public EmeraldRation() {
        super(RationItem.BASE_SETTINGS.rarity(Rarity.EPIC), OcarinaTypes.EMERALD);
    }
}
