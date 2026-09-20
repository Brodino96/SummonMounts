package dev.brodino.summonmounts.items.food;

import dev.brodino.summonmounts.items.FeedItem;
import dev.brodino.summonmounts.items.OcarinaTypes;
import net.minecraft.util.Rarity;

public class SuperiorFeed extends FeedItem {

    public SuperiorFeed() { super(FeedItem.BASE_SETTINGS.rarity(Rarity.EPIC), OcarinaTypes.SUPERIOR); }
}
