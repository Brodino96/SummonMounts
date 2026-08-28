package dev.brodino.summonmounts.items;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.flutes.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public class ItemManager {

    public static final FluteItem COPPER_FLUTE = register(FluteTypes.COPPER.toString(), new CopperFlute());
    public static final FluteItem IRON_FLUTE = register(FluteTypes.IRON.toString(), new IronFlute());
    public static final FluteItem GOLD_FLUTE = register(FluteTypes.GOLD.toString(), new GoldFlute());
    public static final FluteItem DIAMOND_FLUTE = register(FluteTypes.DIAMOND.toString(), new DiamondFlute());
    public static final FluteItem EMERALD_FLUTE = register(FluteTypes.EMERALD.toString(), new EmeraldFlute());


    public static void initialize() {
        SummonMounts.LOGGER.info("Initializing flutes");
    }

    private static FluteItem register(String id, FluteItem flute) {
        return Registry.register(Registry.ITEM, new Identifier(SummonMounts.MOD_ID, id.toLowerCase(Locale.ROOT) + "_flute"), flute);
    }

    public static FluteItem getItemFromEnum(FluteTypes item) {
        return switch (item) {
            case COPPER -> COPPER_FLUTE;
            case IRON -> IRON_FLUTE;
            case GOLD -> GOLD_FLUTE;
            case DIAMOND -> DIAMOND_FLUTE;
            case EMERALD -> EMERALD_FLUTE;
        };
    }

    public static boolean isFlute(Item item) {
        return item != null
                && ( item.equals(COPPER_FLUTE)
                || item.equals(IRON_FLUTE)
                || item.equals(GOLD_FLUTE)
                || item.equals(DIAMOND_FLUTE)
                || item.equals(EMERALD_FLUTE) );
    }
}
