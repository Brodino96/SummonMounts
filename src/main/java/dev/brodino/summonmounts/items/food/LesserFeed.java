package dev.brodino.summonmounts.items.food;

import dev.brodino.summonmounts.items.OcarinaTypes;
import dev.brodino.summonmounts.items.FeedItem;
import net.minecraft.util.Rarity;

public class LesserFeed extends FeedItem {

    public LesserFeed() { super(FeedItem.BASE_SETTINGS.rarity(Rarity.UNCOMMON), OcarinaTypes.LESSER); }
}
