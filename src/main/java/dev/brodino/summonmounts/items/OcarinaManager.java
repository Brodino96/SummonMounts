package dev.brodino.summonmounts.items;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.ocarinas.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public class OcarinaManager {

    public static final OcarinaItem COPPER_OCARINA = register(OcarinaTypes.COPPER.toString(), new CopperOcarina());
    public static final OcarinaItem IRON_OCARINA = register(OcarinaTypes.IRON.toString(), new IronOcarina());
    public static final OcarinaItem GOLD_OCARINA = register(OcarinaTypes.GOLD.toString(), new GoldOcarina());
    public static final OcarinaItem DIAMOND_OCARINA = register(OcarinaTypes.DIAMOND.toString(), new DiamondOcarina());
    public static final OcarinaItem EMERALD_OCARINA = register(OcarinaTypes.EMERALD.toString(), new EmeraldOcarina());


    public static void initialize() {
        SummonMounts.LOGGER.info("Initializing ocarina");
    }

    private static OcarinaItem register(String id, OcarinaItem flute) {
        return Registry.register(Registry.ITEM, new Identifier(SummonMounts.MOD_ID, id.toLowerCase(Locale.ROOT) + "_ocarina"), flute);
    }

    public static OcarinaItem getItemFromEnum(OcarinaTypes item) {
        return switch (item) {
            case COPPER -> COPPER_OCARINA;
            case IRON -> IRON_OCARINA;
            case GOLD -> GOLD_OCARINA;
            case DIAMOND -> DIAMOND_OCARINA;
            case EMERALD -> EMERALD_OCARINA;
        };
    }

    public static boolean isOcarina(Item item) {
        return item != null
                && ( item.equals(COPPER_OCARINA)
                || item.equals(IRON_OCARINA)
                || item.equals(GOLD_OCARINA)
                || item.equals(DIAMOND_OCARINA)
                || item.equals(EMERALD_OCARINA) );
    }
}
