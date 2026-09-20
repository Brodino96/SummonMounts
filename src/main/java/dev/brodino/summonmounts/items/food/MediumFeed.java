package dev.brodino.summonmounts.items.food;

import dev.brodino.summonmounts.items.FeedItem;
import dev.brodino.summonmounts.items.OcarinaTypes;
import net.minecraft.util.Rarity;

public class MediumFeed extends FeedItem {

    public MediumFeed() { super(FeedItem.BASE_SETTINGS.rarity(Rarity.RARE), OcarinaTypes.MEDIUM); }
}
