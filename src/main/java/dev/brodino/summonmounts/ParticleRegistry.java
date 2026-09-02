package dev.brodino.summonmounts;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public class ParticleRegistry {
    public static final DefaultParticleType FEED_PARTICLE = register("feed", FabricParticleTypes.simple());

    public static void initialize() {
        SummonMounts.LOGGER.info("Initializing particles");
    }

    private static DefaultParticleType register(String id, DefaultParticleType type) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(SummonMounts.MOD_ID, id.toLowerCase(Locale.ROOT)), type);
    }

}