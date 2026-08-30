package dev.brodino.summonmounts.items;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.ocarinas.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public class ItemManager {

    public static final OcarinaItem COPPER_OCARINA  = register(OcarinaTypes.COPPER   + "_ocarina",   new CopperOcarina());
    public static final OcarinaItem IRON_OCARINA    = register(OcarinaTypes.IRON     + "_ocarina",   new IronOcarina());
    public static final OcarinaItem GOLD_OCARINA    = register(OcarinaTypes.GOLD     + "_ocarina",   new GoldOcarina());
    public static final OcarinaItem DIAMOND_OCARINA = register(OcarinaTypes.DIAMOND  + "_ocarina",   new DiamondOcarina());
    public static final OcarinaItem EMERALD_OCARINA = register(OcarinaTypes.EMERALD  + "_ocarina",   new EmeraldOcarina());


    public static void initialize() { SummonMounts.LOGGER.info("Initializing ocarina"); }

    private static <T extends Item> T register(String id, T item) {
        return Registry.register(Registry.ITEM, new Identifier(SummonMounts.MOD_ID, id.toLowerCase(Locale.ROOT)), item);
    }

    public static OcarinaItem getOcarinaFromEnum(OcarinaTypes item) {
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
