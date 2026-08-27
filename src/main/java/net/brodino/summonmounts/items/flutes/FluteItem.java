package net.brodino.summonmounts.items.flutes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class FluteItem extends Item {

    public static final Settings SHARED_SETTINGS = new Settings()
            .group(ItemGroup.TOOLS)
            .fireproof()
            .maxCount(1);

    public FluteItem(Settings settings) {
        super(settings);
    }

}
