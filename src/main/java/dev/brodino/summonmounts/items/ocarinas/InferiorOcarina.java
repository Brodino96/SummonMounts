package dev.brodino.summonmounts.items.ocarinas;

import dev.brodino.summonmounts.items.OcarinaItem;
import dev.brodino.summonmounts.items.OcarinaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Rarity;

public class InferiorOcarina extends OcarinaItem {

    public InferiorOcarina() { super(OcarinaItem.BASE_SETTINGS
            .recipeRemainder(Items.RAW_COPPER), OcarinaTypes.INFERIOR); }

    @Override
    public Rarity getRarity(ItemStack stack) { return Rarity.COMMON; }
}
