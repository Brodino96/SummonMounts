package dev.brodino.summonmounts.particle;

import dev.brodino.summonmounts.SummonMounts;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticlesManager {

    public static final DefaultParticleType FEED_PARTICLE = Registry.register(
            Registry.PARTICLE_TYPE, new Identifier(SummonMounts.MOD_ID, "feed"), FabricParticleTypes.simple()
    );

    public static final ParticleType<TintWitchParticleEffect> WITCH_PARTICLE = Registry.register(
            Registry.PARTICLE_TYPE, new Identifier(SummonMounts.MOD_ID, "witch"), FabricParticleTypes.complex(TintWitchParticleEffect.PARAMETERS_FACTORY)
    );

    public static void initialize() { SummonMounts.LOGGER.info("Initializing particles"); }
}