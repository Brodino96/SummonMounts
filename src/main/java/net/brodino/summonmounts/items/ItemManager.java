package net.brodino.summonmounts.items;

import net.brodino.summonmounts.SummonMounts;
import net.brodino.summonmounts.items.flutes.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ItemManager {


    private static final HashMap<FluteTypes, FluteItem> ITEMS = new HashMap<>(){{
        put(FluteTypes.COPPER, new CopperFlute());
        put(FluteTypes.IRON, new IronFlute());
        put(FluteTypes.GOLD, new GoldFlute());
        put(FluteTypes.DIAMOND, new DiamondFlute());
        put(FluteTypes.EMERALD, new EmeraldFlute());
    }};

    public static void initialize() {
        for (Map.Entry<FluteTypes, FluteItem> entry : ITEMS.entrySet()) {
            register(entry.getKey().toString(), entry.getValue());
        }
    }

    private static void register(String id, FluteItem flute) {
        Registry.register(Registry.ITEM, new Identifier(SummonMounts.MOD_ID, id.toLowerCase(Locale.ROOT) + "_flute"), flute);
    }

    public static FluteItem getItemFromEnum(FluteTypes item) {
        return ITEMS.get(item);
    }
}
