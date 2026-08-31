package dev.brodino.summonmounts.items.food;

import dev.brodino.summonmounts.items.OcarinaTypes;
import dev.brodino.summonmounts.items.FeedItem;
import net.minecraft.util.Rarity;

public class GreaterFeed extends FeedItem {

    public GreaterFeed() { super(FeedItem.BASE_SETTINGS.rarity(Rarity.EPIC), OcarinaTypes.GREATER); }
}
