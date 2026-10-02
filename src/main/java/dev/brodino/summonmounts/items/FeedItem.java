package dev.brodino.summonmounts.items;

import net.minecraft.item.ItemGroup;

public class FeedItem extends SummonMountsItem {

    public static final Settings BASE_SETTINGS = new Settings()
            .group(ItemGroup.FOOD)
            .maxCount(64);

    public FeedItem(Settings settings, OcarinaTypes type) { super(settings, type); }
}
