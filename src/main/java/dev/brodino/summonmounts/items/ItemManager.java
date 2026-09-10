package dev.brodino.summonmounts.items;

import dev.brodino.summonmounts.SummonMounts;
import dev.brodino.summonmounts.items.food.*;
import dev.brodino.summonmounts.items.ocarinas.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public class ItemManager {

    public static final OcarinaItem INFERIOR_OCARINA = register(OcarinaTypes.INFERIOR + "_ocarina", new InferiorOcarina());
    public static final OcarinaItem LESSER_OCARINA = register(OcarinaTypes.LESSER + "_ocarina", new LesserOcarina());
    public static final OcarinaItem MEDIUM_OCARINA = register(OcarinaTypes.MEDIUM + "_ocarina", new MediumOcarina());
    public static final OcarinaItem GREATER_OCARINA = register(OcarinaTypes.GREATER + "_ocarina", new GreaterOcarina());
    public static final OcarinaItem SUPERIOR_OCARINA = register(OcarinaTypes.SUPERIOR + "_ocarina", new SuperiorOcarina());

    public static final FeedItem INFERIOR_FEED = register(OcarinaTypes.INFERIOR + "_feed", new InferiorFeed());
    public static final FeedItem LESSER_FEED = register(OcarinaTypes.LESSER + "_feed", new LesserFeed());
    public static final FeedItem MEDIUM_FEED = register(OcarinaTypes.MEDIUM + "_feed", new MediumFeed());
    public static final FeedItem GREATER_FEED = register(OcarinaTypes.GREATER + "_feed", new GreaterFeed());
    public static final FeedItem SUPERIOR_FEED = register(OcarinaTypes.SUPERIOR + "_feed", new SuperiorFeed());

    public static void initialize() { SummonMounts.LOGGER.info("Initializing ocarina"); }

    private static <T extends Item> T register(String id, T item) {
        return Registry.register(Registry.ITEM, new Identifier(SummonMounts.MOD_ID, id.toLowerCase(Locale.ROOT)), item);
    }

    public static OcarinaItem getOcarinaFromEnum(OcarinaTypes type) {
        return switch (type) {
            case INFERIOR -> INFERIOR_OCARINA;
            case LESSER -> LESSER_OCARINA;
            case MEDIUM -> MEDIUM_OCARINA;
            case GREATER -> GREATER_OCARINA;
            case SUPERIOR -> SUPERIOR_OCARINA;
        };
    }
}
