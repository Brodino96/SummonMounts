package dev.brodino.summonmounts.items.food;

import dev.brodino.summonmounts.items.FeedItem;
import dev.brodino.summonmounts.items.OcarinaTypes;
import net.minecraft.util.Rarity;

public class InferiorFeed extends FeedItem {

    public InferiorFeed() { super(FeedItem.BASE_SETTINGS.rarity(Rarity.COMMON), OcarinaTypes.INFERIOR); }
}
