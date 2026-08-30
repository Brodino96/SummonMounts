package dev.brodino.summonmounts.items;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.food.*;
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

    public static final RationItem COPPER_RATION    = register(OcarinaTypes.COPPER    + "_ration",    new CopperRation());
    public static final RationItem IRON_RATION      = register(OcarinaTypes.IRON      + "_ration",    new IronRation());
    public static final RationItem GOLD_RATION      = register(OcarinaTypes.GOLD      + "_ration",    new GoldRation());
    public static final RationItem DIAMOND_RATION   = register(OcarinaTypes.DIAMOND   + "_ration",    new DiamondRation());
    public static final RationItem EMERALD_RATION   = register(OcarinaTypes.EMERALD   + "_ration",    new EmeraldRation());

    public static void initialize() { SummonMounts.LOGGER.info("Initializing ocarina"); }

    private static <T extends Item> T register(String id, T item) {
        return Registry.register(Registry.ITEM, new Identifier(SummonMounts.MOD_ID, id.toLowerCase(Locale.ROOT)), item);
    }

    public static OcarinaItem getOcarinaFromEnum(OcarinaTypes type) {
        return switch (type) {
            case COPPER -> COPPER_OCARINA;
            case IRON -> IRON_OCARINA;
            case GOLD -> GOLD_OCARINA;
            case DIAMOND -> DIAMOND_OCARINA;
            case EMERALD -> EMERALD_OCARINA;
        };
    }

    public static RationItem getRationFromEnum(OcarinaTypes type) {
        return switch (type) {
            case COPPER -> COPPER_RATION;
            case IRON -> IRON_RATION;
            case GOLD -> GOLD_RATION;
            case DIAMOND -> DIAMOND_RATION;
            case EMERALD -> EMERALD_RATION;
        };
    }
}
